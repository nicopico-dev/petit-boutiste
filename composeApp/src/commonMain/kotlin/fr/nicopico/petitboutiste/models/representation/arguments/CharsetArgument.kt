package fr.nicopico.petitboutiste.models.representation.arguments

import fr.nicopico.petitboutiste.models.representation.DataRenderer
import java.nio.charset.Charset

private val DEFAULT: Charset = Charsets.UTF_8

val CharsetArgument = DataRenderer.Argument(
    key = "charset",
    label = "Charset",
    type = ArgumentType.ChoiceType(
        type = Charset::class,
        choices = listOf(Charsets.UTF_8, Charsets.US_ASCII),
        argValueConverter = { Charset.forName(it) },
        choiceConverter = Charset::name,
    ),
    defaultValue = DEFAULT.name(),
)

fun DataRenderer.getCharset(argumentValues: ArgumentValues): Charset {
    return getArgumentValue<Charset>(CharsetArgument.key, argumentValues)
        ?: DEFAULT
}
