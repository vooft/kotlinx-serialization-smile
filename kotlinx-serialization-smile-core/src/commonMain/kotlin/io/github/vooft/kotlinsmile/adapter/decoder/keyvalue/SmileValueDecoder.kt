package io.github.vooft.kotlinsmile.adapter.decoder.keyvalue

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.vooft.kotlinsmile.adapter.decoder.common.valueInt
import io.github.vooft.kotlinsmile.adapter.decoder.common.valueString
import io.github.vooft.kotlinsmile.adapter.decoder.structure.SmileByteArrayDecoder
import io.github.vooft.kotlinsmile.adapter.decoder.structure.SmileListDecoder
import io.github.vooft.kotlinsmile.adapter.decoder.structure.SmileMapDecoder
import io.github.vooft.kotlinsmile.adapter.decoder.structure.SmileObjectDecoder
import io.github.vooft.kotlinsmile.decoder.SmileDecoderSession
import io.github.vooft.kotlinsmile.token.SmileValueToken
import io.github.vooft.kotlinsmile.token.SmileValueToken.BinaryValue
import io.github.vooft.kotlinsmile.token.SmileValueToken.StartArrayMarker
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
                session.skip()
                SmileObjectDecoder(session, serializersModule)
            }

            StructureKind.MAP -> {
                val nextToken = session.peekValueToken()
                require(nextToken == SmileValueToken.StartObjectMarker) { "Expected start object token, but got $nextToken" }
                session.skip()
                SmileMapDecoder(session, serializersModule)
            }

            StructureKind.LIST -> {

                when (val token = session.peekValueToken()) {
                    StartArrayMarker -> {
                        session.skip()
                        SmileListDecoder(session, serializersModule)
                    }

                    BinaryValue -> SmileByteArrayDecoder(session, serializersModule)
                    else -> error("Invalid token for a list: $token")
                }
            }

            else -> TODO("Not implemented yet ${descriptor.kind}")
        }

        logger.debug { "Begin ${descriptor.kind} with ${nested::class}" }

        return nested
    }

    override fun decodeBoolean(): Boolean = session.valueBoolean()

    override fun decodeByte(): Byte = session.valueSmallInteger()

    override fun decodeChar(): Char = session.valueString().single()

    override fun decodeDouble(): Double = session.valueDouble()

    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int = enumDescriptor.getElementIndex(session.valueString())

    override fun decodeFloat(): Float = session.valueFloat()

    override fun decodeInline(descriptor: SerialDescriptor): Decoder = this

    override fun decodeInt(): Int = session.valueInt().toInt()

    override fun decodeLong(): Long = session.valueInt()

    override fun decodeNotNullMark(): Boolean = when (session.peekValueToken()) {
        SmileValueToken.SimpleLiteralNull -> {
            session.skip()
            false
        }

        else -> true
    }

    override fun decodeNull(): Nothing? = null

    override fun decodeShort(): Short = session.valueSmallInteger().toShort()

    override fun decodeString(): String = session.valueString()
}
