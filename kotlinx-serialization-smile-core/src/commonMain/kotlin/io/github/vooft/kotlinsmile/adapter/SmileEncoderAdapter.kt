package io.github.vooft.kotlinsmile.adapter

import io.github.vooft.kotlinsmile.SmileConfig
import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.github.vooft.kotlinsmile.common.isAscii
import io.github.vooft.kotlinsmile.common.isEmpty
import io.github.vooft.kotlinsmile.common.isUnicode
import io.github.vooft.kotlinsmile.common.length
import io.github.vooft.kotlinsmile.common.toSmile
import io.github.vooft.kotlinsmile.encoder.SmileEncoderSession
import io.github.vooft.kotlinsmile.token.SmileKeyToken.KeyLongUnicode
import io.github.vooft.kotlinsmile.token.SmileKeyToken.KeyShortAscii
import io.github.vooft.kotlinsmile.token.SmileKeyToken.KeyShortUnicode
import io.github.vooft.kotlinsmile.token.SmileValueToken.ShortAscii
import io.github.vooft.kotlinsmile.token.SmileValueToken.ShortUnicode
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
class SmileEncoderAdapter(private val config: SmileConfig) : AbstractEncoder() {
    private val builder = ByteArrayBuilder()
    private val session = SmileEncoderSession(builder).apply { header(config) }

    override val serializersModule: SerializersModule = EmptySerializersModule()

    fun toByteArray(): ByteArray = builder.toByteArray()

    override fun encodeValue(value: Any) {
        error("Should not be called for ${value::class}: $value")
    }

    override fun encodeNull() {
        session.nullValue()
    }

    override fun encodeInt(value: Int) {
        when (value) {
            in SmallInteger.values -> session.smallInteger(value)
            else -> TODO("Int value $value not supported yet")
        }
    }

    override fun encodeBoolean(value: Boolean) {
        session.boolean(value)
    }

    override fun encodeByte(value: Byte) = encodeInt(value.toInt())
    override fun encodeShort(value: Short) = encodeInt(value.toInt())

    override fun encodeString(value: String) {
        val smileString = value.toSmile()

        if (smileString.isEmpty) {
            session.emptyString()
        } else if (smileString.isAscii) {
            when (smileString.length) {
                in TinyAscii.lengths -> session.valueTinyAscii(smileString)
                in ShortAscii.lengths -> session.valueShortAscii(smileString)
                else -> session.valueLongAscii(smileString)
            }
        } else {
            when (smileString.length) {
                in TinyUnicode.lengths -> session.valueTinyUnicode(smileString)
                in ShortUnicode.lengths -> session.valueShortUnicode(smileString)
                else -> session.valueLongUnicode(smileString)
            }
        }
    }

    override fun encodeElement(descriptor: SerialDescriptor, index: Int): Boolean {
        if (descriptor.kind == StructureKind.LIST) {
            // this will be invoked for every list item, we want all of them without any keys
            return true
        }

        val name = descriptor.getElementName(index).toSmile()
        println("encodeElement: descriptor=${descriptor.kind}, property name=$name")

        when {
            name.length in KeyLongUnicode.BYTE_LENGTHS -> session.keyLongUnicode(name)
            name.isUnicode && name.length in KeyShortUnicode.BYTE_LENGTHS-> session.keyShortUnicode(name)
            name.isAscii && name.length in KeyShortAscii.BYTE_LENGTHS -> session.keyShortAscii(name)
            else -> error("Element name $name is too long")
        }

        return true
    }

//    override fun beginCollection(descriptor: SerialDescriptor, collectionSize: Int): CompositeEncoder {
//        session.startArray()
//        return this
//    }



    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        println("beginStructure: ${descriptor.kind}")
        when (descriptor.kind) {
            StructureKind.CLASS, StructureKind.OBJECT -> session.startObject()
            StructureKind.LIST -> session.startArray()
            else -> TODO("Not implemented yet ${descriptor.kind}")
        }

        return this
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        when (descriptor.kind) {
            StructureKind.CLASS, StructureKind.OBJECT -> session.endObject()
            StructureKind.LIST -> session.endArray()
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

