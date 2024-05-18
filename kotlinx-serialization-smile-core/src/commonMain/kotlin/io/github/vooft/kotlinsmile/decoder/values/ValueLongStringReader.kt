package io.github.vooft.kotlinsmile.decoder.values

import io.github.vooft.kotlinsmile.common.ByteArrayIterator
import io.github.vooft.kotlinsmile.decoder.raw.nextRawLongString
import io.github.vooft.kotlinsmile.token.SmileValueToken.LongAscii
import io.github.vooft.kotlinsmile.token.SmileValueToken.LongUnicode

interface ValueLongStringReader {
    fun valueLongAscii(): String
    fun valueLongUnicode(): String
}

class ValueLongStringReaderSession(private val iterator: ByteArrayIterator): ValueLongStringReader {
    override fun valueLongAscii(): String {
        val firstByte = iterator.next()
        require(firstByte == LongAscii.firstByte) { "Invalid token for long ascii value: ${firstByte.toUByte().toString(16)}" }

        return iterator.nextRawLongString()
    }

    override fun valueLongUnicode(): String {
        val firstByte = iterator.next()
        require(firstByte == LongUnicode.firstByte) { "Invalid token for long unicode value: ${firstByte.toUByte().toString(16)}" }

        return iterator.nextRawLongString()
    }
}
