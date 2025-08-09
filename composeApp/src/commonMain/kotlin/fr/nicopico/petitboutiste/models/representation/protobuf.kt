package fr.nicopico.petitboutiste.models.representation

import com.google.protobuf.DescriptorProtos.FileDescriptorSet
import com.google.protobuf.Descriptors
import com.google.protobuf.DynamicMessage
import java.io.File

/**
 * Decode a [payload] from a Protobuf message.
 *
 * The Protobuf definition must be provided as a `.desc` file.
 * A `.desc` file can be compiled from a `.proto` file with the following command
 * ```
 * $ protoc --descriptor_set_out=DeviceInfoPush.desc --include_imports DeviceInfoPush.proto
 * ```
 *
 * @param protoFilePath Protobuf definition `.desc` file
 * @param messageType name of the messageType to use for decoding
 */
fun decodeProtobufPayload(payload: ByteArray, protoFilePath: String, messageType: String): String {
    val descriptorSetPath = File(protoFilePath)
    if (!descriptorSetPath.exists()) {
        throw IllegalArgumentException("File does not exist: $protoFilePath")
    }

    val descriptorSet = FileDescriptorSet.parseFrom(descriptorSetPath.inputStream())

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
    return dynamicMessage.toString()
}
