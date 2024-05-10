package io.github.vooft.kotlinsmile.decoder.values

import io.github.vooft.kotlinsmile.common.ByteArrayIterator
import io.github.vooft.kotlinsmile.common.ZigzagSmallInteger
import io.github.vooft.kotlinsmile.token.SmileValueToken.SmallInteger

interface SmallIntegerReader {
    fun smallInteger(): Byte
}

class SmallIntegerReaderSession(private val iterator: ByteArrayIterator): SmallIntegerReader {
    override fun smallInteger(): Byte {
        val byte = iterator.next()
        val zigzag = byte.toUByte() - SmallInteger.offset.toUByte()
        return ZigzagSmallInteger.fromEncoded(zigzag.toInt()).toDecoded()
    }
}
