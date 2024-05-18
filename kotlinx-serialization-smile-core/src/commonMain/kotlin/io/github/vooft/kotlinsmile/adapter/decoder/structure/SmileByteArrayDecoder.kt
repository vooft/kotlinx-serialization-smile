package io.github.vooft.kotlinsmile.adapter.decoder.structure

import io.github.vooft.kotlinsmile.decoder.SmileDecoderSession
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.modules.SerializersModule

@OptIn(ExperimentalSerializationApi::class)
class SmileByteArrayDecoder(
    session: SmileDecoderSession,
    override val serializersModule: SerializersModule
) : AbstractDecoder() {

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

}
