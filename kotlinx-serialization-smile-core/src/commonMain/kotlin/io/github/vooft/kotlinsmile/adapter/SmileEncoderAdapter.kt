package io.github.vooft.kotlinsmile.adapter

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.vooft.kotlinsmile.SmileConfig
import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.github.vooft.kotlinsmile.common.SmileString
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

    private val logger = KotlinLogging.logger { }
    private val builder = ByteArrayBuilder()
    private val session = SmileEncoderSession(builder).apply { header(config) }

    private var isMapKey = false

    override val serializersModule: SerializersModule = EmptySerializersModule()

    fun toByteArray(): ByteArray = builder.toByteArray()

    override fun encodeValue(value: Any) {
        error("Should not be called for ${value::class}: $value")
    }

    override fun encodeNull() {
        logger.info { "encodeNull" }
        session.nullValue()
    }

    override fun encodeInt(value: Int) {
        logger.info { "encodeInt: value=$value"}
        when (value) {
            in SmallInteger.values -> session.smallInteger(value)
            else -> TODO("Int value $value not supported yet")
        }
    }

    override fun encodeBoolean(value: Boolean) {
        logger.info { "encodeBoolean: value=$value"}
        session.boolean(value)
    }

    override fun encodeByte(value: Byte) {
        logger.info { "encodeByte: value=$value"}
        encodeInt(value.toInt())
    }

    override fun encodeShort(value: Short) {
        logger.info { "encodeShort: value=$value"}
        encodeInt(value.toInt())
    }

    override fun encodeString(value: String) {
        logger.info { "encodeString: value=$value"}

        val smileString = value.toSmile()

        if (isMapKey) {
            session.keyString(smileString)
        } else {
            session.valueString(smileString)
        }
    }

    override fun encodeElement(descriptor: SerialDescriptor, index: Int): Boolean {
        return when (descriptor.kind) {
            StructureKind.CLASS, StructureKind.OBJECT -> {
                // key decoding
                val name = descriptor.getElementName(index).toSmile()
                logger.info { "encodeElement: descriptor=${descriptor.kind}, property name=$name" }
                session.keyString(name)
                true
            }
            StructureKind.LIST -> true
            StructureKind.MAP -> {
                // this will be invoked for every key and every value individually
                // for keys (even indexes) we set the flag and encode as key
                // for values (odd indexes) we reset the flag and encode as value
                // the very last item will be value which will naturally reset the flag
                isMapKey = index % 2 == 0
                true
            }
            else -> TODO()
        }
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        logger.info { "beginStructure: ${descriptor.kind}" }
        when (descriptor.kind) {
            StructureKind.CLASS, StructureKind.OBJECT -> session.startObject()
            StructureKind.MAP -> {
//                isMap = true
                session.startObject()
            }
            StructureKind.LIST -> session.startArray()
            else -> TODO("Not implemented yet ${descriptor.kind}")
        }

        return this
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        when (descriptor.kind) {
            StructureKind.CLASS, StructureKind.OBJECT -> session.endObject()
            StructureKind.MAP -> {
//                isMap = false
                session.endObject()
            }
            StructureKind.LIST -> session.endArray()
            else -> TODO("Not implemented yet ${descriptor.kind}")
        }
    }

    override fun <T> encodeSerializableValue(serializer: SerializationStrategy<T>, value: T) {
        super.encodeSerializableValue(serializer, value)
    }
}

private fun SmileEncoderSession.valueString(value: SmileString) {
    if (value.isEmpty) {
        emptyString()
    } else if (value.isAscii) {
        when (value.length) {
            in TinyAscii.lengths -> valueTinyAscii(value)
            in ShortAscii.lengths -> valueShortAscii(value)
            else -> valueLongAscii(value)
        }
    } else {
        when (value.length) {
            in TinyUnicode.lengths -> valueTinyUnicode(value)
            in ShortUnicode.lengths -> valueShortUnicode(value)
            else -> valueLongUnicode(value)
        }
    }
}

private fun SmileEncoderSession.keyString(value: SmileString) {
    when {
        value.length in KeyLongUnicode.BYTE_LENGTHS -> keyLongUnicode(value)
        value.isUnicode && value.length in KeyShortUnicode.BYTE_LENGTHS -> keyShortUnicode(value)
        value.isAscii && value.length in KeyShortAscii.BYTE_LENGTHS -> keyShortAscii(value)
        else -> error("Element name $value is too long")
    }
}

