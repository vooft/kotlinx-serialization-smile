package io.github.vooft.kotlinsmile.adapter

import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.github.vooft.kotlinsmile.common.isUnicode
import io.github.vooft.kotlinsmile.encoder.SmileWriter
import io.github.vooft.kotlinsmile.encoder.SmileWriterSession
import io.github.vooft.kotlinsmile.token.SmileKeyToken.KeyLongUnicode
import io.github.vooft.kotlinsmile.token.SmileKeyToken.KeyShortAscii
import io.github.vooft.kotlinsmile.token.SmileKeyToken.KeyShortUnicode
import io.github.vooft.kotlinsmile.token.SmileValueToken.SmallInteger
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

@OptIn(ExperimentalSerializationApi::class)
class SmileEncoderAdapter : AbstractEncoder() {
    private val builder = ByteArrayBuilder()
    private val session: SmileWriter = SmileWriterSession(builder).apply { header() }

    override val serializersModule: SerializersModule = EmptySerializersModule()

    fun toByteArray(): ByteArray = builder.toByteArray()

    override fun encodeValue(value: Any) {
        error("Should not be called for ${value::class}: $value")
    }

    override fun encodeInt(value: Int) {
        when (value) {
            in SmallInteger.VALUES_RANGE -> session.smallInteger(value)
            else -> TODO("Int value $value not supported yet")
        }
    }

    override fun encodeByte(value: Byte) = encodeInt(value.toInt())
    override fun encodeShort(value: Short) = encodeInt(value.toInt())

    override fun encodeString(value: String) {
        super.encodeString(value)
    }

    override fun encodeElement(descriptor: SerialDescriptor, index: Int): Boolean {
        val name = descriptor.getElementName(index)

        // TODO: create wrapper and calculate everything once
        if (name.isUnicode()) {
            val encoded = name.encodeToByteArray()
            when (encoded.size) {
                in KeyShortUnicode.BYTE_LENGTHS -> session.keyShortUnicode(name)
                in KeyLongUnicode.BYTE_LENGTHS -> session.keyLongUnicode(name)
                else -> error("Element name $name is too long")
            }
        } else {
            when (name.length) {
                in KeyShortAscii.BYTE_LENGTHS -> session.keyShortAscii(name)
                in KeyLongUnicode.BYTE_LENGTHS -> session.keyLongUnicode(name)
                else -> error("Element name $name is too long")
            }
        }

        return super.encodeElement(descriptor, index)
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        when (descriptor.kind) {
            StructureKind.CLASS, StructureKind.OBJECT -> session.startObject()
            else -> TODO("Not implemented yet ${descriptor.kind}")
        }

        return super.beginStructure(descriptor)
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        when (descriptor.kind) {
            StructureKind.CLASS, StructureKind.OBJECT -> session.endObject()
            else -> TODO("Not implemented yet ${descriptor.kind}")
        }

        super.endStructure(descriptor)
    }

    override fun <T> encodeSerializableValue(serializer: SerializationStrategy<T>, value: T) {
        super.encodeSerializableValue(serializer, value)
    }
}

//fun <T> encodeToList(serializer: SerializationStrategy<T>, value: T): List<Any> {
//    val encoder = SmileEncoderAdapter()
//    encoder.encodeSerializableValue(serializer, value)
//    return encoder.list
//}
//
//inline fun <reified T> encodeToList(value: T) = encodeToList(serializer(), value)

