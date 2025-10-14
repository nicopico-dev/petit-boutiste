package fr.nicopico.petitboutiste.scripting

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.io.File
import java.net.URLClassLoader
import kotlin.reflect.typeOf
import kotlin.script.experimental.api.EvaluationResult
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.ScriptEvaluationConfiguration
import kotlin.script.experimental.api.defaultImports
import kotlin.script.experimental.api.providedProperties
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.baseClassLoader
import kotlin.script.experimental.jvm.dependenciesFromClassloader
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

// TODO Allow usage of ExperimentalStdlibApi API in the KTS script like `ByteArray.toHexString()`
class ScriptHost(
    private val timeout: Duration = 5.seconds,
    private val createApi: () -> PetitBoutisteApi,
) {
    private val host = BasicJvmScriptingHost()

    suspend fun evalFile(
        file: File,
        args: List<String> = emptyList(),
    ): ResultWithDiagnostics<EvaluationResult> = withContext(Dispatchers.IO) {
        withTimeout(timeout) {
            val api = createApi()

            // TODO Use a new classloader per run (light isolation)
            val cl: ClassLoader = ScriptHost::class.java.classLoader

            val compilationCfg = ScriptCompilationConfiguration {
                jvm {
                    // Use the application classpath
                    dependenciesFromClassloader(
                        classLoader = cl,
                        wholeClasspath = true,
                    )
                }
                providedProperties(
                    "args" to typeOf<List<String>>(),
                    "ptb" to typeOf<PetitBoutisteApi>(),
                )
                defaultImports(
                    "kotlin.io.*",
                    "kotlin.math.*"
                )
            }

            val evalCfg = ScriptEvaluationConfiguration {
                jvm {
                    baseClassLoader(cl)
                }
                providedProperties(
                    "args" to args,
                    "ptb" to api,
                )
            }

            host.eval(file.toScriptSource(), compilationCfg, evalCfg)
        }
    }

    private fun defaultClasspath(): List<File> {
        // Fallback to the app’s own classpath
        val urls = (ClassLoader.getSystemClassLoader() as? URLClassLoader)?.urLs.orEmpty()
        return urls.mapNotNull { it.toURI().path?.let(::File) }.filter { it.exists() }
    }
}
