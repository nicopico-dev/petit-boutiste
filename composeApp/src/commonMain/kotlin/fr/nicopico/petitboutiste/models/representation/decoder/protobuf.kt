/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.representation.decoder

import com.google.protobuf.Descriptors
import fr.nicopico.petitboutiste.models.representation.DataRenderer
import fr.nicopico.petitboutiste.models.representation.DataRenderer.Argument
import fr.nicopico.petitboutiste.models.representation.arguments.ArgumentType
import fr.nicopico.petitboutiste.models.representation.arguments.ArgumentType.FileType
import fr.nicopico.petitboutiste.models.representation.arguments.ArgumentValues
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.io.files.Path

const val ARG_PROTO_FILE_KEY = "protoFile"
const val ARG_MESSAGE_TYPE_KEY = "messageType"

val protobufArguments = listOf(
    Argument(
        key = ARG_PROTO_FILE_KEY,
        label = "Protobuf '.desc' file",
        type = FileType,
        hint = """Compiled from the .proto file(s) using:
            | `$ protoc --descriptor_set_out=/PATH/TO/output.desc --include_imports /PATH/TO/PROTO/*.proto --proto_path /PATH/TO/PROTO/`
            | Note that PATH must be absolute and cannot use '~'
        """.trimMargin()
    ),
    Argument(
        key = ARG_MESSAGE_TYPE_KEY,
        label = "MessageType",
        type = ArgumentType.ChoiceType(
            type = String::class,
            getChoices = { arguments ->
                arguments
                    .map {
                        DataRenderer.Protobuf.getArgumentValue<Path>(ARG_PROTO_FILE_KEY, it)
                    }
                    .distinctUntilChanged()
                    .map { protoFileArgument ->
                        if (protoFileArgument != null) {
                            getMessageTypeDescriptors(protoFileArgument)
                                .map { it.name }
                                .sorted()
                        } else emptyList()
                    }
            },
            argValueConverter = { it },
            choiceConverter = { it },
        ),
        hint = "Name of the message, as defined in the .proto file",
    ),
)

expect suspend fun DataRenderer.decodeProtobuf(byteArray: ByteArray, argumentValues: ArgumentValues): String

expect suspend fun getMessageTypeDescriptors(protoFilePath: Path): List<Descriptors.Descriptor>
