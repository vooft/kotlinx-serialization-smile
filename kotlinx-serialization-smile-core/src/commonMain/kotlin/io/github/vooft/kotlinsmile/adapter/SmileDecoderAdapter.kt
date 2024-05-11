package io.github.vooft.kotlinsmile.adapter

import io.github.vooft.kotlinsmile.common.ByteArrayIteratorImpl
import io.github.vooft.kotlinsmile.decoder.SmileDecoderSession
import io.github.vooft.kotlinsmile.token.SmileKeyToken
import io.github.vooft.kotlinsmile.token.SmileValueToken.EndArrayMarker
import io.github.vooft.kotlinsmile.token.SmileValueToken.EndObjectMarker
import io.github.vooft.kotlinsmile.token.SmileValueToken.LongAscii
import io.github.vooft.kotlinsmile.token.SmileValueToken.LongUnicode
import io.github.vooft.kotlinsmile.token.SmileValueToken.ShortAscii
import io.github.vooft.kotlinsmile.token.SmileValueToken.ShortUnicode
import io.github.vooft.kotlinsmile.token.SmileValueToken.SimpleLiteral
import io.github.vooft.kotlinsmile.token.SmileValueToken.SmallInteger
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
    private val session = SmileDecoderSession(ByteArrayIteratorImpl(data))
    private val config = session.header()

    private lateinit var currentValueDescriptor: SerialDescriptor

    override val serializersModule: SerializersModule = EmptySerializersModule()

    override fun decodeValue(): Any {
        val nextToken = session.peekValueToken()
        return when (nextToken) {
            LongAscii -> TODO()
            SimpleLiteral -> TODO()
            SmallInteger -> session.smallInteger().castIfNecessary(currentValueDescriptor)
            EndArrayMarker -> TODO()
            EndObjectMarker -> TODO()
            LongUnicode -> TODO()
            StartArrayMarker -> TODO()
            StartObjectMarker -> TODO()
            ShortAscii -> session.valueShortAscii()
            ShortUnicode -> session.valueShortUnicode()
            TinyAscii -> session.valueTinyAscii()
            TinyUnicode -> session.valueTinyUnicode()
        }
    }

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        val nextToken = session.peekKeyToken()
        val propertyName = when (nextToken) {
            SmileKeyToken.KeyLongUnicode -> session.keyLongUnicode()
            SmileKeyToken.KeyShortAscii -> session.keyShortAscii()
            SmileKeyToken.KeyShortUnicode -> session.keyShortUnicode()
            SmileKeyToken.KeyEndObjectMarker -> return CompositeDecoder.DECODE_DONE
            SmileKeyToken.KeyStartObjectMarker -> TODO()
        }

        val index = descriptor.getElementIndex(propertyName)
        currentValueDescriptor = descriptor.getElementDescriptor(index)
        return index
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        when (descriptor.kind) {
            StructureKind.CLASS, StructureKind.OBJECT -> println("ok class")
            else -> TODO("Not implemented yet ${descriptor.kind}")
        }

        val nextToken = session.peekValueToken()
        require(nextToken == StartObjectMarker) { "Expected start object token, but got $nextToken" }
        session.skip()
        return this
    }
}

@OptIn(ExperimentalSerializationApi::class)
private fun Byte.castIfNecessary(descriptor: SerialDescriptor): Any {
    return when (descriptor.serialName) {
        Byte::class.qualifiedName -> this
        Short::class.qualifiedName -> this.toShort()
        Int::class.qualifiedName -> this.toInt()
        Long::class.qualifiedName -> this.toLong()
        else -> throw IllegalArgumentException("Unsupported type ${descriptor.serialName}")
    }
}

