package io.github.vooft.kotlinsmile.adapter.encoder.common

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule
import kotlin.concurrent.Volatile

@OptIn(ExperimentalSerializationApi::class)
class MutableEncoderDelegate(@Volatile var delegate: Encoder) : Encoder {
    override val serializersModule: SerializersModule get() = delegate.serializersModule

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        return delegate.beginStructure(descriptor)
    }

    override fun encodeBoolean(value: Boolean) {
        delegate.encodeBoolean(value)
    }

    override fun encodeByte(value: Byte) {
        delegate.encodeByte(value)
    }

    override fun encodeChar(value: Char) {
        delegate.encodeChar(value)
    }

    override fun encodeDouble(value: Double) {
        delegate.encodeDouble(value)
    }

    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
        delegate.encodeEnum(enumDescriptor, index)
    }

    override fun encodeFloat(value: Float) {
        delegate.encodeFloat(value)
    }

    override fun encodeInline(descriptor: SerialDescriptor): Encoder {
        return delegate.encodeInline(descriptor)
    }

    override fun encodeInt(value: Int) {
        delegate.encodeInt(value)
    }

    override fun encodeLong(value: Long) {
        delegate.encodeLong(value)
    }

    override fun encodeNotNullMark() {
        delegate.encodeNotNullMark()
    }

    override fun encodeNull() {
        delegate.encodeNull()
    }

    override fun encodeShort(value: Short) {
        delegate.encodeShort(value)
    }

    override fun encodeString(value: String) {
        delegate.encodeString(value)
    }
}
