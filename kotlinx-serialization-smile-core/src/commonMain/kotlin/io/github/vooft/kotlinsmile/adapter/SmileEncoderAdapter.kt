package io.github.vooft.kotlinsmile.adapter

import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.github.vooft.kotlinsmile.common.isAscii
import io.github.vooft.kotlinsmile.common.isUnicode
import io.github.vooft.kotlinsmile.common.length
import io.github.vooft.kotlinsmile.common.toSmile
import io.github.vooft.kotlinsmile.encoder.SmileWriter
import io.github.vooft.kotlinsmile.encoder.SmileWriterSession
import io.github.vooft.kotlinsmile.token.SmileKeyToken.KeyLongUnicode
import io.github.vooft.kotlinsmile.token.SmileKeyToken.KeyShortAscii
import io.github.vooft.kotlinsmile.token.SmileKeyToken.KeyShortUnicode
import io.github.vooft.kotlinsmile.token.SmileValueToken.SmallInteger
import io.github.vooft.kotlinsmile.token.SmileValueToken.TinyAscii
import io.github.vooft.kotlinsmile.token.SmileValueToken.TinyUnicode
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
        val smileString = value.toSmile()

        when {
            smileString.isAscii && smileString.length in TinyAscii.BYTE_LENGTHS -> session.valueTinyAscii(smileString)
            smileString.isUnicode && smileString.length in TinyUnicode.BYTE_LENGTHS -> session.valueTinyUnicode(smileString)
            else -> error("String $value is too long")
        }
    }

    override fun encodeElement(descriptor: SerialDescriptor, index: Int): Boolean {
        val name = descriptor.getElementName(index).toSmile()

        when {
            name.length in KeyLongUnicode.BYTE_LENGTHS -> session.keyLongUnicode(name)
            name.isUnicode && name.length in KeyShortUnicode.BYTE_LENGTHS-> session.keyShortUnicode(name)
            name.isAscii && name.length in KeyShortAscii.BYTE_LENGTHS -> session.keyShortAscii(name)
            else -> error("Element name $name is too long")
        }

        return true
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        when (descriptor.kind) {
            StructureKind.CLASS, StructureKind.OBJECT -> session.startObject()
            else -> TODO("Not implemented yet ${descriptor.kind}")
        }

        return this
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        when (descriptor.kind) {
            StructureKind.CLASS, StructureKind.OBJECT -> session.endObject()
            else -> TODO("Not implemented yet ${descriptor.kind}")
        }
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

