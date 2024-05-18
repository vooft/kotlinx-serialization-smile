package io.github.vooft.kotlinsmile.decoder.values

import io.github.vooft.kotlinsmile.common.ByteArrayIterator
import io.github.vooft.kotlinsmile.common.ZigzagInteger
import io.github.vooft.kotlinsmile.decoder.raw.nextRawInt
import io.github.vooft.kotlinsmile.decoder.raw.nextRawLong
import io.github.vooft.kotlinsmile.token.SmileValueToken.LongInteger
import io.github.vooft.kotlinsmile.token.SmileValueToken.RegularInteger
import io.github.vooft.kotlinsmile.token.SmileValueToken.SmallInteger
import io.github.vooft.kotlinsmile.token.contains

interface IntegerReader {
    fun valueSmallInteger(): Byte
    fun valueRegularInteger(): Int
    fun valueLongInteger(): Long
}

class IntegerReaderSession(private val iterator: ByteArrayIterator) : IntegerReader {
    override fun valueSmallInteger(): Byte {
        val byte = iterator.next()
        require(byte in SmallInteger) {
            "Invalid token for small integer ${SmallInteger.tokenRange}, actual: $byte"
        }

        val zigzag = byte.toUByte() - SmallInteger.offset.toUByte()
        return ZigzagInteger.decode(zigzag.toInt()).toByte()
    }

    override fun valueRegularInteger(): Int {
        val firstByte = iterator.next()
        require(firstByte == RegularInteger.firstByte) {
            "Invalid token for regular integer ${RegularInteger.firstByte}, actual: $firstByte"
        }

        val zigzag = iterator.nextRawInt()
        return ZigzagInteger.decode(zigzag)
    }

    override fun valueLongInteger(): Long {
        val firstByte = iterator.next()
        require(firstByte == LongInteger.firstByte) {
            "Invalid token for long integer ${LongInteger.firstByte}, actual: $firstByte"
        }

        val zigzag = iterator.nextRawLong()
        return ZigzagInteger.decode(zigzag)
    }
}

