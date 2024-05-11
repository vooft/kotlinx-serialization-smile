package io.github.vooft.kotlinsmile.adapter

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.vooft.kotlinsmile.common.ByteArrayIteratorImpl
import io.github.vooft.kotlinsmile.decoder.SmileDecoderSession
import io.github.vooft.kotlinsmile.token.SmileKeyToken
import io.github.vooft.kotlinsmile.token.SmileKeyToken.KeyEndObjectMarker
import io.github.vooft.kotlinsmile.token.SmileKeyToken.KeyLongUnicode
import io.github.vooft.kotlinsmile.token.SmileKeyToken.KeyShortAscii
import io.github.vooft.kotlinsmile.token.SmileKeyToken.KeyShortUnicode
import io.github.vooft.kotlinsmile.token.SmileKeyToken.SmileKeyStringToken
import io.github.vooft.kotlinsmile.token.SmileValueToken.EndArrayMarker
import io.github.vooft.kotlinsmile.token.SmileValueToken.LongAscii
import io.github.vooft.kotlinsmile.token.SmileValueToken.LongUnicode
import io.github.vooft.kotlinsmile.token.SmileValueToken.ShortAscii
import io.github.vooft.kotlinsmile.token.SmileValueToken.ShortUnicode
import io.github.vooft.kotlinsmile.token.SmileValueToken.SimpleLiteralBoolean
import io.github.vooft.kotlinsmile.token.SmileValueToken.SimpleLiteralEmptyString
import io.github.vooft.kotlinsmile.token.SmileValueToken.SimpleLiteralNull
import io.github.vooft.kotlinsmile.token.SmileValueToken.SmallInteger
import io.github.vooft.kotlinsmile.token.SmileValueToken.SmileStringToken
import io.github.vooft.kotlinsmile.token.SmileValueToken.StartArrayMarker
import io.github.vooft.kotlinsmile.token.SmileValueToken.StartObjectMarker
import io.github.vooft.kotlinsmile.token.SmileValueToken.TinyAscii
import io.github.vooft.kotlinsmile.token.SmileValueToken.TinyUnicode
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

@OptIn(ExperimentalSerializationApi::class)
class SmileDecoderAdapter(data: ByteArray) : AbstractDecoder() {

    private val logger = KotlinLogging.logger { }

    private val session = SmileDecoderSession(ByteArrayIteratorImpl(data))
    private val config = session.header()

    private var currentListElementIndex = 0
    private var isMapKey = true

    override val serializersModule: SerializersModule = EmptySerializersModule()

    init {
        println(config)
    }

    override fun decodeByte(): Byte {
        return session.smallInteger()
    }

    override fun decodeInt(): Int {
        // TODO: add support for large ones
        return session.smallInteger().toInt()
    }

    override fun decodeShort(): Short {
        return session.smallInteger().toShort()
    }

    override fun decodeString(): String {
        if (isMapKey) {
            val token = session.peekStringKeyToken()
            return when (token) {
                KeyLongUnicode -> session.keyLongUnicode()
                KeyShortAscii -> session.keyShortAscii()
                KeyShortUnicode -> session.keyShortUnicode()
            }
        } else {
            val token = session.peekStringValueToken()
            return when (token) {
                LongAscii -> session.valueLongAscii()
                SimpleLiteralEmptyString -> session.valueEmptyString()
                LongUnicode -> session.valueLongUnicode()
                ShortAscii -> session.valueShortAscii()
                ShortUnicode -> session.valueShortUnicode()
                TinyAscii -> session.valueTinyAscii()
                TinyUnicode -> session.valueTinyUnicode()
            }
        }
    }

    override fun decodeValue(): Any {
        val token = session.peekValueToken()
        return when (token) {
            SimpleLiteralBoolean -> session.valueBoolean()
            EndArrayMarker -> TODO()
            StartArrayMarker -> TODO()
            is SmileStringToken, SimpleLiteralNull, SmallInteger, StartObjectMarker -> error("$token should be handled separately")
        }.also {
            logger.info { "Decoded value $token: $it" }
        }
    }

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        logger.info { "decodeElementIndex: $descriptor" }

        when (descriptor.kind) {
            StructureKind.CLASS, StructureKind.OBJECT -> {
                // key decoding
                val nextToken = session.peekKeyToken()
                logger.info { "Decoding object key $nextToken" }

                val propertyName = session.keyString(nextToken) ?: return CompositeDecoder.DECODE_DONE
                return descriptor.getElementIndex(propertyName)
            }

            StructureKind.LIST -> {
                // value decoding
                val nextValue = session.peekValueToken()
                if (nextValue == EndArrayMarker) {
                    session.skip()
                    return CompositeDecoder.DECODE_DONE
                } else {
                    return currentListElementIndex++
                }
            }

            StructureKind.MAP -> {
                if (!isMapKey) {
                    val nextKey = session.peekKeyToken()
                    logger.info { "Decoding map key $nextKey" }
                    if (nextKey == KeyEndObjectMarker) {
                        session.skip()
                        return CompositeDecoder.DECODE_DONE
                    } else {
                        isMapKey = true
                        return currentListElementIndex++
                    }
                } else {
                    logger.info { "Decoding map value ${session.peekValueToken()}" }
                    isMapKey = false
                    return currentListElementIndex++
                }
            }

            else -> TODO()
        }


    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        when (descriptor.kind) {
            StructureKind.CLASS, StructureKind.OBJECT -> {
                val nextToken = session.peekValueToken()
                require(nextToken == StartObjectMarker) { "Expected start object token, but got $nextToken" }
            }

            StructureKind.MAP -> {
                val nextToken = session.peekValueToken()
                require(nextToken == StartObjectMarker) { "Expected start object token, but got $nextToken" }
                currentListElementIndex = 0
            }

            StructureKind.LIST -> {
                val nextToken = session.peekValueToken()
                require(nextToken == StartArrayMarker) { "Expected start array token, but got $nextToken" }
                currentListElementIndex = 0
            }

            else -> TODO("Not implemented yet ${descriptor.kind}")
        }

        logger.info { "Begin ${descriptor.kind}" }

        session.skip()
        return this
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        logger.info { "End ${descriptor.kind}" }
        super.endStructure(descriptor)
    }

    override fun decodeNotNullMark(): Boolean {
        when (session.peekValueToken()) {
            SimpleLiteralNull -> {
                session.skip()
                return false
            }

            else -> return true
        }

    }
}

private fun SmileDecoderSession.peekStringValueToken(): SmileStringToken {
    return when (val token = peekValueToken()) {
        is SmileStringToken -> token
        else -> error("Unexpected token $token")
    }
}

private fun SmileDecoderSession.peekStringKeyToken(): SmileKeyStringToken {
    return when (val token = peekKeyToken()) {
        is SmileKeyStringToken -> token
        else -> error("Unexpected token $token")
    }
}

private fun SmileDecoderSession.keyString(token: SmileKeyToken): String? {
    return when (token) {
        KeyLongUnicode -> keyLongUnicode()
        KeyShortAscii -> keyShortAscii()
        KeyShortUnicode -> keyShortUnicode()
        KeyEndObjectMarker -> {
            skip()
            return null
        }
    }
}
