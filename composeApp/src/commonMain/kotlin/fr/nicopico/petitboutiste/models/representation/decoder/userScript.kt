package fr.nicopico.petitboutiste.models.representation.decoder

import fr.nicopico.petitboutiste.log
import fr.nicopico.petitboutiste.logError
import fr.nicopico.petitboutiste.models.representation.DataRenderer
import fr.nicopico.petitboutiste.models.representation.DataRenderer.Argument
import fr.nicopico.petitboutiste.models.representation.arguments.ArgumentType.FileType
import fr.nicopico.petitboutiste.models.representation.arguments.ArgumentValues
import fr.nicopico.petitboutiste.scripting.PetitBoutisteApi
import fr.nicopico.petitboutiste.scripting.ScriptHost
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.script.experimental.api.ResultValue
import kotlin.script.experimental.api.ResultWithDiagnostics.Failure
import kotlin.script.experimental.api.ResultWithDiagnostics.Success
import kotlin.script.experimental.api.isError
import kotlin.script.experimental.api.valueOrNull

private const val ARG_USER_SCRIPT_FILE_KEY = "protoFile"

val USER_SCRIPT_ARGUMENTS = listOf(
    Argument(
        key = ARG_USER_SCRIPT_FILE_KEY,
        label = "script file",
        type = FileType,
    ),
)

// TODO Make dataRenderer suspendable
fun DataRenderer.decodeUserScript(byteArray: ByteArray, argumentValues: ArgumentValues): String {
    require(this == DataRenderer.UserScript)
    val scriptFile: File = getArgumentValue(ARG_USER_SCRIPT_FILE_KEY, argumentValues)!!

    val host = ScriptHost {
        object : PetitBoutisteApi {
            override fun debug(message: String) = log(message)
            override fun error(message: String) = logError(message)
            override fun getPayload(): ByteArray = byteArray
        }
    }

    log("Running user script $scriptFile on ${byteArray.toHexString()}...")
    val result = runBlocking {
        host.evalFile(scriptFile)
    }

    log("result is $result")
    return when (result) {
        is Failure -> result.reports
            .filter { it.isError() }
            .joinToString(
                prefix = "ERROR: ",
                separator = "\n",
                transform = { diagnostic ->
                    with(diagnostic) {
                        exception?.stackTraceToString()
                            ?: message
                    }
                },
            )

        is Success<*> -> when (val rv = result.valueOrNull()?.returnValue) {
            is ResultValue.Value -> rv.value.toString()
            is ResultValue.Error -> "ERROR: ${rv.error}"
            is ResultValue.NotEvaluated -> "ERROR: NotEvaluated"
            is ResultValue.Unit -> "ERROR: No return value"
            null -> "ERROR: return value is null"
        }
    }
}
