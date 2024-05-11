package io.github.vooft.kotlinsmile.adapter.encoder.keyvalue

import io.github.vooft.kotlinsmile.adapter.encoder.common.keyString
import io.github.vooft.kotlinsmile.common.toSmile
import io.github.vooft.kotlinsmile.encoder.SmileEncoderSession
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule

@OptIn(ExperimentalSerializationApi::class)
class SmileKeyEncoder(
    private val session: SmileEncoderSession,
    override val serializersModule: SerializersModule
) : Encoder {

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        error("Can not begin a structure in a key mode")
    }

    override fun encodeBoolean(value: Boolean) = encodeString(value.toString())

    override fun encodeByte(value: Byte) = encodeString(value.toString())

    override fun encodeChar(value: Char) = encodeString(value.toString())

    override fun encodeDouble(value: Double) = encodeString(value.toString())

    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) = encodeString(enumDescriptor.getElementName(index))

    override fun encodeFloat(value: Float) = encodeString(value.toString())

    override fun encodeInline(descriptor: SerialDescriptor): Encoder = this

    override fun encodeInt(value: Int) = encodeString(value.toString())

    override fun encodeLong(value: Long) = encodeString(value.toString())

    override fun encodeNull(): Unit = error("Null key is not allowed in Smile")

    override fun encodeShort(value: Short) = encodeString(value.toString())

    override fun encodeString(value: String) = session.keyString(value.toSmile())
}
