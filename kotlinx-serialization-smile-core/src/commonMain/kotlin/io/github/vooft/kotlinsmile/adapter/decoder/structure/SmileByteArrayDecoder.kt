package io.github.vooft.kotlinsmile.adapter.decoder.structure

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.vooft.kotlinsmile.decoder.SmileDecoderSession
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.modules.SerializersModule

@OptIn(ExperimentalSerializationApi::class)
class SmileByteArrayDecoder(
    private val session: SmileDecoderSession,
    override val serializersModule: SerializersModule
) : AbstractDecoder() {

    private val logger = KotlinLogging.logger {  }

    private val data = session.valueBinary()
    private var index = -1

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        require(descriptor.kind == StructureKind.LIST) { "Only list deserialization is supported" }

        if (++index >= data.size) {
            return CompositeDecoder.DECODE_DONE
        }

        return index
    }

    override fun decodeValue(): Any {
        error("Invalid data type to decode from ByteArray")
    }

    override fun decodeByte(): Byte = data[index]

    override fun endStructure(descriptor: SerialDescriptor) {
        logger.debug { "end structure" }
        super.endStructure(descriptor)
    }
}
