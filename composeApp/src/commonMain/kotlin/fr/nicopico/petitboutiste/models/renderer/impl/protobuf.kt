package fr.nicopico.petitboutiste.models.renderer.impl

import com.google.protobuf.DescriptorProtos
import com.google.protobuf.Descriptors
import com.google.protobuf.DynamicMessage
import com.google.protobuf.util.JsonFormat
import fr.nicopico.petitboutiste.models.renderer.DataRenderer
import fr.nicopico.petitboutiste.models.renderer.DataRenderer.Argument
import fr.nicopico.petitboutiste.models.renderer.arguments.ArgumentType.FileType
import fr.nicopico.petitboutiste.models.renderer.arguments.ArgumentType.StringType
import fr.nicopico.petitboutiste.models.renderer.arguments.ArgumentValues
import java.io.File

private const val ARG_PROTO_FILE_KEY = "protoFile"
private const val ARG_MESSAGE_TYPE_KEY = "messageType"

val PROTOBUF_ARGUMENTS = listOf(
    Argument(
        key = ARG_PROTO_FILE_KEY,
        label = "Protobuf '.desc' file",
        type = FileType,
    ),
    Argument(
        key = ARG_MESSAGE_TYPE_KEY,
        label = "MessageType",
        type = StringType,
    ),
)

fun DataRenderer.decodeProtobuf(byteArray: ByteArray, argumentValues: ArgumentValues): String {
    require(this == DataRenderer.Protobuf)
    return decodeProtobufPayload(
        payload = byteArray,
        protoFile = getArgumentValue(ARG_PROTO_FILE_KEY, argumentValues)!!,
        messageType = getArgumentValue(ARG_MESSAGE_TYPE_KEY, argumentValues)!!,
    )
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
private fun decodeProtobufPayload(payload: ByteArray, protoFile: File, messageType: String): String {
    if (!protoFile.exists()) {
        throw IllegalArgumentException("File does not exist: $protoFile")
    }

    val descriptorSet = DescriptorProtos.FileDescriptorSet.parseFrom(protoFile.inputStream())

    // Retrieve the descriptor for the specified message type
    val fileDescriptors = descriptorSet.fileList
        .map {
            Descriptors.FileDescriptor.buildFrom(it, emptyArray())
        }

    val messageTypeDescriptor = fileDescriptors
        .flatMap { it.messageTypes }
        .firstOrNull { it.name == messageType }
        ?: throw IllegalArgumentException("Message type $messageType not found in .proto file")

    // Parse a dynamic Protobuf message from the byte array
    val dynamicMessage = DynamicMessage.parseFrom(messageTypeDescriptor, payload)

    // Convert the dynamic Protobuf message to a JSON string
    return JsonFormat.printer().print(dynamicMessage)
}
