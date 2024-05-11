package io.github.vooft.kotlinsmile.decoder.values

import io.github.vooft.kotlinsmile.common.ByteArrayIterator
import io.github.vooft.kotlinsmile.token.SmileMarkers
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

        return readLongString()
    }

    override fun valueLongUnicode(): String {
        val firstByte = iterator.next()
        require(firstByte == LongUnicode.firstByte) { "Invalid token for long unicode value: ${firstByte.toUByte().toString(16)}" }

        return readLongString()
    }

    private fun readLongString(): String {
        var counter = 0
        while (iterator.next() != SmileMarkers.STRING_END_MARKER)  {
            counter++
        }

        iterator.rollback(counter + 1)

        val encoded = iterator.next(counter)
        require(iterator.next() == SmileMarkers.STRING_END_MARKER) { "Invalid end marker for long unicode key" }

        return encoded.decodeToString()
    }
}
