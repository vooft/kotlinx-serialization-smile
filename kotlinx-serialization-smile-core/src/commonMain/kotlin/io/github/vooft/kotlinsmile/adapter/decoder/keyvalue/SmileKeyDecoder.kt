package io.github.vooft.kotlinsmile.adapter.decoder.keyvalue

import io.github.vooft.kotlinsmile.adapter.decoder.common.keyString
import io.github.vooft.kotlinsmile.decoder.SmileDecoderSession
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.modules.SerializersModule

@OptIn(ExperimentalSerializationApi::class)
class SmileKeyDecoder(
    private val session: SmileDecoderSession,
    override val serializersModule: SerializersModule
) : Decoder {
    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        error("Unable to decode structure in a key mode, structure: ${descriptor.kind}")
    }

    override fun decodeBoolean(): Boolean = decodeString().toBoolean()

    override fun decodeByte(): Byte = decodeString().toByte()

    override fun decodeChar(): Char = decodeString().single()

    override fun decodeDouble(): Double {
        TODO("Not yet implemented")
    }

    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int {
        TODO("Not yet implemented")
    }

    override fun decodeFloat(): Float {
        TODO("Not yet implemented")
    }

    override fun decodeInline(descriptor: SerialDescriptor): Decoder = this

    override fun decodeInt(): Int = decodeString().toInt()

    override fun decodeLong(): Long = decodeString().toLong()

    @ExperimentalSerializationApi
    override fun decodeNotNullMark(): Boolean = error("Null key is not allowed in Smile")

    @ExperimentalSerializationApi
    override fun decodeNull(): Nothing = error("Null key is not allowed in Smile")

    override fun decodeShort(): Short = decodeString().toShort()

    override fun decodeString(): String = session.keyString()
}
