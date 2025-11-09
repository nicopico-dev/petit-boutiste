package fr.nicopico.macos

import fr.nicopico.macos.jni.JNIEnvVar
import fr.nicopico.macos.jni.jclass
import kotlinx.cinterop.CPointer

// Follow JNI naming: Java_<package>_<class>_<method>
// IMPORTANT: these elements should match the *target* -> the function with the `external` modifier
@CName("Java_fr_nicopico_macos_MacosBridge_jniObserveTheme")
fun jniObserveTheme(env: CPointer<JNIEnvVar>, clazz: jclass): Int {
    return 42
}
