package fr.nicopico.petitboutiste.models.renderer.arguments

import fr.nicopico.petitboutiste.models.Endianness
import fr.nicopico.petitboutiste.models.renderer.DataRenderer

private val DEFAULT: Endianness = Endianness.BigEndian

val EndiannessArgument = DataRenderer.Argument(
    key = "endianness",
    label = "Endianness",
    type = ArgumentType.ChoiceType(
        type = Endianness::class,
        choices = Endianness.entries,
        argValueConverter = Endianness::valueOf,
        choiceConverter = Endianness::name,
    ),
    defaultValue = DEFAULT.name,
)

fun DataRenderer.getEndianness(argumentValues: ArgumentValues): Endianness {
    return getArgumentValue<Endianness>(EndiannessArgument.key, argumentValues)
        ?: DEFAULT
}
