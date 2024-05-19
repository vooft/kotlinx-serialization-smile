package io.github.vooft.kotlinsmile.adapter.encoder.keyvalue

import io.github.vooft.kotlinsmile.adapter.encoder.common.valueInteger
import io.github.vooft.kotlinsmile.adapter.encoder.common.valueString
import io.github.vooft.kotlinsmile.adapter.encoder.structure.SmileByteArrayEncoder
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

            else -> error("Not supported ${descriptor.kind}")
        }
    }

    override fun beginCollection(descriptor: SerialDescriptor, collectionSize: Int): CompositeEncoder {
        return when {
            descriptor.isByteArray() -> SmileByteArrayEncoder(session, collectionSize, serializersModule)
            else -> beginStructure(descriptor)
        }
    }

    override fun encodeBoolean(value: Boolean) = session.valueBoolean(value)

    override fun encodeByte(value: Byte) = session.valueSmallInteger(value.toInt())

    override fun encodeChar(value: Char) = session.valueString(value.toString().toSmile())

    override fun encodeDouble(value: Double) = session.valueDouble(value)

    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) =
        session.valueString(enumDescriptor.getElementName(index).toSmile())

    override fun encodeFloat(value: Float) = session.valueFloat(value)

    override fun encodeInline(descriptor: SerialDescriptor) = this

    override fun encodeInt(value: Int) = session.valueInteger(value.toLong())

    override fun encodeLong(value: Long) = session.valueInteger(value)

    override fun encodeNull() = session.valueNull()

    override fun encodeShort(value: Short) = session.valueInteger(value.toLong())

    override fun encodeString(value: String) = session.valueString(value.toSmile())
}

@OptIn(ExperimentalSerializationApi::class)
private fun SerialDescriptor.isByteArray() = kind == StructureKind.LIST && serialName == "kotlin.ByteArray"
