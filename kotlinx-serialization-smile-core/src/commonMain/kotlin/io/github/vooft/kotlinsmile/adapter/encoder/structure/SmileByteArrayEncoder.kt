package io.github.vooft.kotlinsmile.adapter.encoder.structure

import io.github.vooft.kotlinsmile.encoder.SmileEncoderSession
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.modules.SerializersModule

@OptIn(ExperimentalSerializationApi::class)
class SmileByteArrayEncoder(
    private val session: SmileEncoderSession,
    size: Int,
    override val serializersModule: SerializersModule
) : AbstractEncoder() {

    private val buffer = ByteArray(size)
    private var index = 0

    override fun encodeValue(value: Any) {
        error("Invalid data type to encode in ByteArray: ${value::class}")
    }

    override fun encodeNull() {
        error("ByteArray can not contain null values")
    }

    override fun encodeByte(value: Byte) {
        buffer[index++] = value
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        require(index == buffer.size) { "Incomplete data was written, expected: ${buffer.size}, actual: $index"}
        session.valueBinary(buffer)
    }
}
