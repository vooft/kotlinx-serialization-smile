package io.github.vooft.kotlinsmile.adapter.encoder.keyvalue

import io.github.vooft.kotlinsmile.adapter.encoder.common.valueInteger
import io.github.vooft.kotlinsmile.adapter.encoder.common.valueString
import io.github.vooft.kotlinsmile.adapter.encoder.structure.SmileListEncoder
import io.github.vooft.kotlinsmile.adapter.encoder.structure.SmileMapEncoder
import io.github.vooft.kotlinsmile.adapter.encoder.structure.SmileObjectEncoder
import io.github.vooft.kotlinsmile.common.toSmile
import io.github.vooft.kotlinsmile.encoder.SmileEncoderSession
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule

@OptIn(ExperimentalSerializationApi::class)
class SmileValueEncoder(
    val session: SmileEncoderSession,
    override val serializersModule: SerializersModule
) : Encoder {

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        return when (descriptor.kind) {
            StructureKind.CLASS, StructureKind.OBJECT -> {
                session.startObject()
                SmileObjectEncoder(session, serializersModule)
            }
            StructureKind.MAP -> {
                session.startObject()
                SmileMapEncoder(session, serializersModule)
            }
            StructureKind.LIST -> {
                session.startArray()
                SmileListEncoder(session, serializersModule)
            }
            else -> TODO("Not implemented yet ${descriptor.kind}")
        }
    }

    override fun encodeBoolean(value: Boolean) = session.boolean(value)

    override fun encodeByte(value: Byte) = session.smallInteger(value.toInt())

    override fun encodeChar(value: Char) = session.valueString(value.toString().toSmile())

    override fun encodeDouble(value: Double) {
        TODO("Not yet implemented")
    }

    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
        TODO("Not yet implemented")
    }

    override fun encodeFloat(value: Float) {
        TODO("Not yet implemented")
    }

    override fun encodeInline(descriptor: SerialDescriptor) = this

    override fun encodeInt(value: Int) = session.valueInteger(value.toLong())

    override fun encodeLong(value: Long) = session.valueInteger(value.toLong())

    override fun encodeNull() = session.nullValue()

    override fun encodeShort(value: Short) = session.valueInteger(value.toLong())

    override fun encodeString(value: String) = session.valueString(value.toSmile())
}
