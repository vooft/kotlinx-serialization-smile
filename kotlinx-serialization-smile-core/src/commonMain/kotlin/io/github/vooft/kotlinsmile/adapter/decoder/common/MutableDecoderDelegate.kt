package io.github.vooft.kotlinsmile.adapter.decoder.common

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.modules.SerializersModule
import kotlin.concurrent.Volatile

class MutableDecoderDelegate(@Volatile var delegate: Decoder) : Decoder {

    override val serializersModule: SerializersModule
        get() = delegate.serializersModule

    override fun beginStructure(descriptor: SerialDescriptor) = delegate.beginStructure(descriptor)

    override fun decodeBoolean() = delegate.decodeBoolean()

    override fun decodeByte() = delegate.decodeByte()

    override fun decodeChar() = delegate.decodeChar()

    override fun decodeDouble() = delegate.decodeDouble()

    override fun decodeEnum(enumDescriptor: SerialDescriptor) = delegate.decodeEnum(enumDescriptor)

    override fun decodeFloat() = delegate.decodeFloat()

    override fun decodeInline(descriptor: SerialDescriptor) = delegate.decodeInline(descriptor)

    override fun decodeInt() = delegate.decodeInt()

    override fun decodeLong() = delegate.decodeLong()

    @ExperimentalSerializationApi
    override fun decodeNotNullMark() = delegate.decodeNotNullMark()

    @ExperimentalSerializationApi
    override fun decodeNull() = delegate.decodeNull()

    override fun decodeShort() = delegate.decodeShort()

    override fun decodeString() = delegate.decodeString()
}
