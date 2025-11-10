package fr.nicopico.macos

import fr.nicopico.macos.jni.JNIEnvVar
import fr.nicopico.macos.jni.JNI_EDETACHED
import fr.nicopico.macos.jni.JNI_OK
import fr.nicopico.macos.jni.JNI_VERSION_1_8
import fr.nicopico.macos.jni.JavaVMVar
import fr.nicopico.macos.jni.jclass
import fr.nicopico.macos.jni.jint
import fr.nicopico.macos.jni.jmethodID
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.cstr
import kotlinx.cinterop.invoke
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.value
import platform.Foundation.NSDistributedNotificationCenter
import platform.Foundation.NSNotification
import platform.Foundation.NSOperationQueue
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

// Keep global references to JVM and method
private var gJvm: CPointer<JavaVMVar>? = null
private var gMacosBridgeClass: jclass? = null
private var gNotifyMethodId: jmethodID? = null

private var gObserverToken: Any? = null

// Called by JVM when the library is loaded
@Suppress("unused")
@CName("JNI_OnLoad")
fun jniOnLoad(vm: CPointer<JavaVMVar>, reserved: CPointer<*>?): jint {
    gJvm = vm
    return JNI_VERSION_1_8
}

// Follow JNI naming: Java_<package>_<class>_<method>
// IMPORTANT: these elements should match the *target* -> the function with the `external` modifier
@Suppress("unused")
@CName("Java_fr_nicopico_macos_MacosBridge_jniStartObservingTheme")
fun jniStartObservingTheme(env: CPointer<JNIEnvVar>, clazz: jclass) {
    log("Starting observing theme")
    ensureCachedIds(env)

    // Register the notification observer on the main queue
    val notifName = "AppleInterfaceThemeChangedNotification"
    val center = NSDistributedNotificationCenter.defaultCenter()

    // Ensure we add observer on the main thread/queue
    dispatch_async(dispatch_get_main_queue()) {
        // Remove previous observer if any
        gObserverToken?.let { token ->
            center.removeObserver(token)
            gObserverToken = null
        }

        gObserverToken = center.addObserverForName(
            name = notifName,
            `object` = null,
            queue = NSOperationQueue.mainQueue,
            usingBlock = { _: NSNotification? ->
                log("MacOS theme changed!!")
                onMacosThemeChanged()
            }
        )
    }
}

@Suppress("unused")
@CName("Java_fr_nicopico_macos_MacosBridge_jniStopObservingTheme")
fun jniStopObservingTheme(jniEnv: CPointer<JNIEnvVar>, clazz: jclass) {
    log("Stop observing theme")
    val center = NSDistributedNotificationCenter.defaultCenter()
    gObserverToken?.let { token ->
        dispatch_async(dispatch_get_main_queue()) {
            center.removeObserver(token)
            gObserverToken = null
        }
    }
}

private fun onMacosThemeChanged() {
    withJvmEnv { env ->
        ensureCachedIds(env)

        val jni = env.pointed.pointed!!

        // CallStaticVoidMethod is too hard to use, I wasn't able to figure it out.
        // Kotlin/Native should use `CallStaticVoidMethodA`, with an array of jvalue for the arguments.
        // If the function takes no arguments, pass `null`.
        jni.CallStaticVoidMethodA!!(env, gMacosBridgeClass, gNotifyMethodId, null)

        // Ensure there was no exception thrown by the notify method
        checkJniException(env)
    }
}

private fun ensureCachedIds(env: CPointer<JNIEnvVar>) {
    if (gMacosBridgeClass != null && gNotifyMethodId != null) return
    log("Cache MacosBridge notifyMethodId")

    // Ensure all memory allocated in this block is freed when exiting the block
    memScoped {
        val jni = env.pointed.pointed!!

        // FindClass returns a LocalRef to the Java class, only valid until the end of this function
        // GlobalRef stay valid across JNI calls so we can use it in other functions
        val localClass = jni.FindClass!!(env, "fr/nicopico/macos/MacosBridge".cstr.ptr)
        gMacosBridgeClass = jni.NewGlobalRef!!(env, localClass) as jclass
        jni.DeleteLocalRef!!(env, localClass) // localClass is no longer needed, cleanup
        log("gMacosBridgeClass = $gMacosBridgeClass")

        gNotifyMethodId = jni.GetStaticMethodID!!(
            env,
            gMacosBridgeClass,
            "notifyThemeChanged".cstr.ptr,
            // JNI Signature
            // `(Ljava/lang/String;)V` would be a function with a String parameter, returning Void
            "()V".cstr.ptr,
        )
        log("gNotifyMethodId = $gNotifyMethodId")
    }
}

private inline fun withJvmEnv(block: (CPointer<JNIEnvVar>) -> Unit) {
    val vm = gJvm ?: return

    memScoped {
        // NOTE: no-arg alloc is an *extension* function and must be imported
        // -> `import kotlinx.cinterop.alloc`
        val envVar = alloc<CPointerVar<JNIEnvVar>>()
        val jvmFns = vm.pointed.pointed!!

        val res = jvmFns.GetEnv!!(
            vm,
            envVar.ptr.reinterpret(),
            JNI_VERSION_1_8,
        )

        // Attach/Detach the JNI thread only if necessary
        var attachedHere = false
        val env: CPointer<JNIEnvVar> = when (res) {
            JNI_OK -> envVar.value!! // already attached
            JNI_EDETACHED -> {
                log("Re-attach JNI thread")
                jvmFns.AttachCurrentThread!!(vm, envVar.ptr.reinterpret(), null)
                    .check("AttachCurrentThread") { return }
                attachedHere = true
                envVar.value!!
            }
            else -> return@memScoped
        }

        try {
            block(env)
        } finally {
            if (attachedHere) {
                jvmFns.DetachCurrentThread!!(vm)
                    .check("DetachCurrentThread") { return }
            }
        }
    }
}

private fun checkJniException(env: CPointer<JNIEnvVar>) {
    val jni = env.pointed.pointed!!

    val hasException = jni.ExceptionCheck!!(env) != 0.toUByte()
    if (hasException) {
        // Log Java stacktrace to stderr
        jni.ExceptionDescribe!!(env)

        // Clear the exception
        jni.ExceptionClear!!(env)
    }
}

/**
 * Use this function to check C-style function return values.
 * If the value is different from 0, [onFail] will execute and the caller execution flow will be interrupted.
 */
private inline fun Int.check(context: String, onFail: () -> Nothing) {
    if (this != 0) {
        log("$context error: $this")
        onFail()
    }
}

private fun log(msg: String) {
    println("[JNI] $msg")
}
