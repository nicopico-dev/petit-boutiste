package fr.nicopico.petitboutiste.models.representation.decoder

import com.google.protobuf.DescriptorProtos
import com.google.protobuf.Descriptors
import com.google.protobuf.DynamicMessage
import com.google.protobuf.util.JsonFormat
import fr.nicopico.petitboutiste.models.representation.DataRenderer
import fr.nicopico.petitboutiste.models.representation.DataRenderer.Argument
import fr.nicopico.petitboutiste.models.representation.arguments.ArgumentType.FileType
import fr.nicopico.petitboutiste.models.representation.arguments.ArgumentType.StringType
import fr.nicopico.petitboutiste.models.representation.arguments.ArgumentValues
import java.io.File

private const val ARG_PROTO_FILE_KEY = "protoFile"
private const val ARG_MESSAGE_TYPE_KEY = "messageType"

val PROTOBUF_ARGUMENTS = listOf(
    Argument(
        key = ARG_PROTO_FILE_KEY,
        label = "Protobuf '.desc' file",
        type = FileType,
        // TODO Check the command to use to compile a desc file
        hint = """Compiled from the .proto file(s) using:
            | `$ protoc --descriptor_set_out=/PATH/TO/output.desc --include_imports /PATH/TO/PROTO/*.proto --proto_path /PATH/TO/PROTO/`
            | Note that PATH must be absolute and cannot use '~'
        """.trimMargin()
    ),
    Argument(
        key = ARG_MESSAGE_TYPE_KEY,
        label = "MessageType",
        type = StringType,
        hint = "Name of the message, as defined in the .proto file"
    ),
)

private val protobufPrinter = JsonFormat.printer()
    .preservingProtoFieldNames()

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
    return protobufPrinter.print(dynamicMessage)
}
