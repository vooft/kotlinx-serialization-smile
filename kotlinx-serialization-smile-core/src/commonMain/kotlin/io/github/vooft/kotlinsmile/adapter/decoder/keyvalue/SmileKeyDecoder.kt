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

    override fun decodeBoolean(): Boolean = TODO()

    override fun decodeByte(): Byte = TODO()

    override fun decodeChar(): Char = TODO()

    override fun decodeDouble(): Double {
        TODO("Not yet implemented")
    }

    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int {
        TODO("Not yet implemented")
    }

    override fun decodeFloat(): Float {
        TODO("Not yet implemented")
    }

    override fun decodeInline(descriptor: SerialDescriptor): Decoder {
        TODO("Not yet implemented")
    }

    override fun decodeInt(): Int = TODO()

    override fun decodeLong(): Long {
        TODO("Not yet implemented")
    }

    @ExperimentalSerializationApi
    override fun decodeNotNullMark(): Boolean = TODO()

    @ExperimentalSerializationApi
    override fun decodeNull(): Nothing? = null

    override fun decodeShort(): Short = TODO()

    override fun decodeString(): String = session.keyString()
}
