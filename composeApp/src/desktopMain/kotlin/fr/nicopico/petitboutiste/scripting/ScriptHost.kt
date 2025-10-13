package fr.nicopico.petitboutiste.scripting

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.io.File
import java.net.URLClassLoader
import kotlin.script.experimental.api.EvaluationResult
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.api.implicitReceivers
import kotlin.script.experimental.api.providedProperties
import kotlin.script.experimental.api.with
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class ScriptHost(
    private val createApi: () -> PetitBoutisteApi,
    private val classpath: List<File> = emptyList(),
    private val timeout: Duration = 5.seconds,
) {
    private val host = BasicJvmScriptingHost()

    suspend fun evalFile(
        file: File,
        args: List<String> = emptyList(),
    ): ResultWithDiagnostics<EvaluationResult> = withContext(Dispatchers.IO) {
        withTimeout(timeout) {
            val api = createApi()

            // New classloader per run (light isolation)
            val cl = URLClassLoader(
                (classpath.ifEmpty { defaultClasspath() })
                    .map { it.toURI().toURL() }
                    .toTypedArray(),
                null /* no parent -> fewer accidental leaks */
            )

            val compilationCfg = PtbScriptCompilationConfiguration
                .with {
                    // FIXME `jvm` is not visible ??
                    /*jvm {
                        // Override with our per-run classloader
                        baseClassLoader(cl)
                        // You may add curated dependencies here as well
                    }*/
                }

            val evalCfg = PtbScriptEvaluationConfiguration
                .with {
                    implicitReceivers(api)
                    providedProperties(
                        "args" to args
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
