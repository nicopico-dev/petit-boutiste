/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.representation.decoder

import com.google.protobuf.DescriptorProtos
import com.google.protobuf.Descriptors
import com.google.protobuf.DynamicMessage
import com.google.protobuf.util.JsonFormat
import fr.nicopico.petitboutiste.models.representation.DataRenderer
import fr.nicopico.petitboutiste.models.representation.DataRenderer.Argument
import fr.nicopico.petitboutiste.models.representation.arguments.ArgumentType
import fr.nicopico.petitboutiste.models.representation.arguments.ArgumentType.FileType
import fr.nicopico.petitboutiste.models.representation.arguments.ArgumentValues
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File

private const val ARG_PROTO_FILE_KEY = "protoFile"
private const val ARG_MESSAGE_TYPE_KEY = "messageType"

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
                        DataRenderer.Protobuf.getArgumentValue<File>(ARG_PROTO_FILE_KEY, it)
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

private val protobufPrinter = JsonFormat.printer()
    .preservingProtoFieldNames()

suspend fun DataRenderer.decodeProtobuf(byteArray: ByteArray, argumentValues: ArgumentValues): String {
    require(this == DataRenderer.Protobuf)
    return decodeProtobufPayload(
        payload = byteArray,
        protoFile = getArgumentValue(ARG_PROTO_FILE_KEY, argumentValues)!!,
        messageType = getArgumentValue(ARG_MESSAGE_TYPE_KEY, argumentValues)!!,
    )
}

private suspend fun getMessageTypeDescriptors(protoFile: File): List<Descriptors.Descriptor> {
    val fileDescriptors = withContext(Dispatchers.IO) {
        if (!protoFile.exists()) {
            throw IllegalArgumentException("File does not exist: $protoFile")
        }
        val descriptorSet = DescriptorProtos.FileDescriptorSet.parseFrom(protoFile.inputStream())
        descriptorSet.fileList.map { Descriptors.FileDescriptor.buildFrom(it, emptyArray()) }
    }

    return fileDescriptors
        .flatMap { it.messageTypes }
        .filterNotNull()
}

/**
 * Decode a [payload] from a Protobuf message.
 *
 * The Protobuf definition must be provided as a `.desc` file.
 * A `.desc` file can be compiled from a `.proto` file with the following command
 * ```
 * $ protoc --descriptor_set_out=DeviceInfoPush.desc --include_imports DeviceInfoPush.proto
 * ```
 *
 * @param protoFile Protobuf definition `.desc` file
 * @param messageType name of the messageType to use for decoding
 */
private suspend fun decodeProtobufPayload(payload: ByteArray, protoFile: File, messageType: String): String {
    val messageTypeDescriptor = getMessageTypeDescriptors(protoFile)
        .firstOrNull { it.name == messageType }
        ?: throw IllegalArgumentException("Message type $messageType not found in .proto file")

    // Parse a dynamic Protobuf message from the byte array
    val dynamicMessage = DynamicMessage.parseFrom(messageTypeDescriptor, payload)

    // Convert the dynamic Protobuf message to a JSON string
    return protobufPrinter.print(dynamicMessage)
}
