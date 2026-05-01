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
import fr.nicopico.petitboutiste.utils.file.absolutePath
import fr.nicopico.petitboutiste.utils.file.asSource
import fr.nicopico.petitboutiste.utils.file.exists
import fr.nicopico.petitboutiste.utils.file.lastModified
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.io.files.Path
import kotlinx.io.readByteArray

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

private val protobufPrinter = JsonFormat.printer()
    .preservingProtoFieldNames()
    .alwaysPrintFieldsWithNoPresence()

suspend fun DataRenderer.decodeProtobuf(byteArray: ByteArray, argumentValues: ArgumentValues): String {
    require(this == DataRenderer.Protobuf)
    return decodeProtobufPayload(
        payload = byteArray,
        protoFilePath = requireNotNull(getArgumentValue(ARG_PROTO_FILE_KEY, argumentValues)) {
            "Missing argument $ARG_PROTO_FILE_KEY"
        },
        messageType = requireNotNull(getArgumentValue(ARG_MESSAGE_TYPE_KEY, argumentValues)) {
            "Missing argument $ARG_MESSAGE_TYPE_KEY"
        },
    )
}

private data class DescriptorCacheKey(val path: String, val lastModified: Long)
private val descriptorCache = mutableMapOf<DescriptorCacheKey, List<Descriptors.Descriptor>>()
private val cacheMutex = Mutex()

private suspend fun getMessageTypeDescriptors(protoFilePath: Path): List<Descriptors.Descriptor> {
    val cacheKey = DescriptorCacheKey(protoFilePath.absolutePath, protoFilePath.lastModified())
    cacheMutex.withLock {
        descriptorCache[cacheKey]?.let { return it }
    }

    val descriptors = withContext(Dispatchers.IO) {
        require(protoFilePath.exists()) {
            "File does not exist: $protoFilePath"
        }

        val descriptorSet = protoFilePath.asSource().use {
            val data = it.readByteArray()
            DescriptorProtos.FileDescriptorSet.parseFrom(data)
        }
        DescriptorParser(descriptorSet).parse()
            .flatMap { it.messageTypes }
            .filterNotNull()
    }

    cacheMutex.withLock {
        descriptorCache[cacheKey] = descriptors
    }
    return descriptors
}

private class DescriptorParser(
    private val descriptorSet: DescriptorProtos.FileDescriptorSet
) {
    private val parsedDescriptor: MutableMap<String, Descriptors.FileDescriptor> = mutableMapOf()
    private val descriptorProtoByName: Map<String, DescriptorProtos.FileDescriptorProto> =
        descriptorSet.fileList.associateBy { it.name }

    fun parse(): List<Descriptors.FileDescriptor> {
        return descriptorSet.fileList.map(::toFileDescriptor)
    }

    private fun toFileDescriptor(
        fileDescriptorProto: DescriptorProtos.FileDescriptorProto
    ): Descriptors.FileDescriptor {
        return getFileDescriptorFromName(fileDescriptorProto.name, fileDescriptorProto)
    }

    // false-positive on UnreachableCode (Detekt 1.23.8 with type resolution)
    @Suppress("UnreachableCode")
    private fun getFileDescriptorFromName(
        name: String,
        initialDescriptorProto: DescriptorProtos.FileDescriptorProto? = null,
    ): Descriptors.FileDescriptor {
        return parsedDescriptor.getOrPut(name) {
            val descriptorProto = initialDescriptorProto
                ?: descriptorProtoByName[name]
                ?: throw IllegalArgumentException("Missing dependency: $name. Make sure to include imports in the descriptor set.")

            val dependencies = descriptorProto.dependencyList.map {
                getFileDescriptorFromName(it)
            }
            Descriptors.FileDescriptor.buildFrom(descriptorProto, dependencies.toTypedArray())
        }
    }
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
 * @param protoFilePath Protobuf definition `.desc` file
 * @param messageType name of the messageType to use for decoding
 */
private suspend fun decodeProtobufPayload(payload: ByteArray, protoFilePath: Path, messageType: String): String {
    val messageTypeDescriptor = getMessageTypeDescriptors(protoFilePath)
        .firstOrNull { it.name == messageType }

    requireNotNull(messageTypeDescriptor) {
        "Message type $messageType not found in descriptor set (.desc file)"
    }

    // Parse a dynamic Protobuf message from the byte array
    val dynamicMessage = DynamicMessage.parseFrom(messageTypeDescriptor, payload)

    // Convert the dynamic Protobuf message to a JSON string
    return protobufPrinter.print(dynamicMessage)
}
