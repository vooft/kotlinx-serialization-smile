package io.github.vooft.kotlinsmile.adapter.decoder.keyvalue

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.vooft.kotlinsmile.adapter.decoder.common.valueInt
import io.github.vooft.kotlinsmile.adapter.decoder.common.valueString
import io.github.vooft.kotlinsmile.adapter.decoder.structure.SmileListDecoder
import io.github.vooft.kotlinsmile.adapter.decoder.structure.SmileMapDecoder
import io.github.vooft.kotlinsmile.adapter.decoder.structure.SmileObjectDecoder
import io.github.vooft.kotlinsmile.decoder.SmileDecoderSession
import io.github.vooft.kotlinsmile.token.SmileValueToken
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.modules.SerializersModule

@OptIn(ExperimentalSerializationApi::class)
class SmileValueDecoder(
    private val session: SmileDecoderSession,
    override val serializersModule: SerializersModule
) : Decoder {

    private val logger = KotlinLogging.logger { }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        val nested = when (descriptor.kind) {
            StructureKind.CLASS, StructureKind.OBJECT -> {
                val nextToken = session.peekValueToken()
                require(nextToken == SmileValueToken.StartObjectMarker) { "Expected start object token, but got $nextToken" }
                SmileObjectDecoder(session, serializersModule)
            }

            StructureKind.MAP -> {
                val nextToken = session.peekValueToken()
                require(nextToken == SmileValueToken.StartObjectMarker) { "Expected start object token, but got $nextToken" }
                SmileMapDecoder(session, serializersModule)
            }

            StructureKind.LIST -> {
                val nextToken = session.peekValueToken()
                require(nextToken == SmileValueToken.StartArrayMarker) { "Expected start array token, but got $nextToken" }
                SmileListDecoder(session, serializersModule)
            }

            else -> TODO("Not implemented yet ${descriptor.kind}")
        }

        logger.info { "Begin ${descriptor.kind}" }

        session.skip()
        return nested
    }

    override fun decodeBoolean(): Boolean = session.valueBoolean()

    override fun decodeByte(): Byte = session.smallInteger()

    override fun decodeChar(): Char = session.smallInteger().toInt().toChar()

    override fun decodeDouble(): Double {
        TODO("Not yet implemented")
    }

    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int {
        TODO("Not yet implemented")
    }

    override fun decodeFloat(): Float {
        TODO("Not yet implemented")
    }

    override fun decodeInline(descriptor: SerialDescriptor): Decoder {
        TODO("Not yet implemented")
    }

    override fun decodeInt(): Int = session.valueInt()

    override fun decodeLong(): Long {
        TODO("Not yet implemented")
    }

    override fun decodeNotNullMark(): Boolean = when (session.peekValueToken()) {
        SmileValueToken.SimpleLiteralNull -> {
            session.skip()
            false
        }

        else -> true
    }

    override fun decodeNull(): Nothing? = null

    override fun decodeShort(): Short = session.smallInteger().toShort()

    override fun decodeString(): String = session.valueString()
}
