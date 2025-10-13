package fr.nicopico.petitboutiste.scripting

import kotlin.reflect.typeOf
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.ScriptEvaluationConfiguration
import kotlin.script.experimental.api.defaultImports
import kotlin.script.experimental.api.implicitReceivers
import kotlin.script.experimental.api.providedProperties

@KotlinScript(
    fileExtension = "ptb.kts",
    compilationConfiguration = PtbScriptCompilationConfiguration::class,
    evaluationConfiguration = PtbScriptEvaluationConfiguration::class,
)
abstract class ScriptTemplate

interface PetitBoutisteApi {
    fun log(message: String)
    fun error(message: String)
    fun getPayload(): ByteArray
    fun setResult(result: String)
}

object PtbScriptCompilationConfiguration : ScriptCompilationConfiguration({
    // Provide our API as an implicit receiver: scripts can call its methods directly.
    implicitReceivers(PetitBoutisteApi::class)

    // If you want to pass args or services:
    providedProperties(
        "args" to typeOf<List<String>>()
    )

    // Allow default imports so scripts look clean:
    defaultImports(
        "kotlin.io.*",
        "kotlin.math.*"
        // optionally "my.app.dsl.*"
    )

    // FIXME `jvm` is not visible ??
    /*// Put only what you need on the compilation classpath:
    jvm {
        // Pick one of the following strategies:

        // 1) Use current app classpath (simple; least restrictive)
        dependenciesFromClassContext(MyScriptTemplate::class, wholeClasspath = true)

        // 2) Or build a curated classpath (preferred for control):
        // dependencies(listOf(File("your-api-only.jar"), ...))
    }

    // Optional: let scripts resolve external libs with @file:DependsOn
    refineConfiguration {
        onAnnotations(DependsOn::class, Repository::class) {
            // Accept dependsOn annotations and add jars to classpath
            val resolver = BasicJvmScriptDependenciesResolver()
            context.compilationConfiguration.with {
                configurationWithDependenciesFromClassloader(context)
            }.asSuccess()
        }
    }*/
})

object PtbScriptEvaluationConfiguration : ScriptEvaluationConfiguration({
    // Bind run-time values for providedProperties / implicitReceivers at eval time.
    // We'll set these dynamically before each evaluation.
})
