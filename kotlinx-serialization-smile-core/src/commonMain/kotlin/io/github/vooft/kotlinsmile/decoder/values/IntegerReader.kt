package io.github.vooft.kotlinsmile.decoder.values

import io.github.vooft.kotlinsmile.common.ByteArrayIterator
import io.github.vooft.kotlinsmile.common.ZigzagInteger
import io.github.vooft.kotlinsmile.token.SmileValueToken.LongInteger
import io.github.vooft.kotlinsmile.token.SmileValueToken.RegularInteger
import io.github.vooft.kotlinsmile.token.SmileValueToken.SmallInteger
import kotlin.experimental.xor

interface IntegerReader {
    fun smallInteger(): Byte
    fun regularInteger(): Int
    fun longInteger(): Long
}

class IntegerReaderSession(private val iterator: ByteArrayIterator) : IntegerReader {
    override fun smallInteger(): Byte {
        val byte = iterator.next()
        val zigzag = byte.toUByte() - SmallInteger.offset.toUByte()
        return ZigzagInteger.decode(zigzag.toInt()).toByte()
    }

    override fun regularInteger(): Int {
        val firstByte = iterator.next()
        require(firstByte == RegularInteger.firstByte) {
            "Invalid token for regular integer ${RegularInteger.firstByte}, actual: $firstByte"
        }

        var zigzag = 0
        var byte = iterator.next()
        while (byte.toUInt() and FIRST_BIT_MASK == 0u) {
            zigzag = (zigzag shl 7) or byte.toInt()
            byte = iterator.next()
        }

        zigzag = (zigzag shl 6) or (byte xor FIRST_BIT_MASK.toByte()).toInt()
        return ZigzagInteger.decode(zigzag)
    }

    override fun longInteger(): Long {
        val firstByte = iterator.next()
        require(firstByte == LongInteger.firstByte) {
            "Invalid token for long integer ${LongInteger.firstByte}, actual: $firstByte"
        }

        var zigzag = 0L
        var byte = iterator.next()
        while (byte.toUInt() and FIRST_BIT_MASK == 0u) {
            zigzag = (zigzag shl 7) or byte.toLong()
            byte = iterator.next()
        }

        zigzag = (zigzag shl 6) or (byte xor FIRST_BIT_MASK.toByte()).toLong()
        return ZigzagInteger.decode(zigzag)
    }
}

private const val FIRST_BIT_MASK = 0b1000_0000u
