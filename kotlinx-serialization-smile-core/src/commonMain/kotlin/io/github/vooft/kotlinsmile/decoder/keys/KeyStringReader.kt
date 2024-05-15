package io.github.vooft.kotlinsmile.decoder.keys

import io.github.vooft.kotlinsmile.common.ByteArrayIterator
import io.github.vooft.kotlinsmile.decoder.shared.DecodingSmileSharedStorage
import io.github.vooft.kotlinsmile.token.SmileKeyToken.KeyLongUnicode
import io.github.vooft.kotlinsmile.token.SmileKeyToken.KeyShortAscii
import io.github.vooft.kotlinsmile.token.SmileKeyToken.KeyShortUnicode
import io.github.vooft.kotlinsmile.token.SmileMarkers
import io.github.vooft.kotlinsmile.token.contains

interface KeyStringReader {
    fun keyShortAscii(): String
    fun keyShortUnicode(): String
    fun keyLongUnicode(): String
}

class KeyStringReaderSession(
    private val iterator: ByteArrayIterator,
    private val sharedStorage: DecodingSmileSharedStorage
) : KeyStringReader {
    override fun keyShortAscii(): String {
        val byte = iterator.next()
        require(byte in KeyShortAscii) { "Invalid token for short ascii key: ${byte.toUByte().toString(16)}" }

        val length = byte.toUByte() - KeyShortAscii.offset.toUByte()
        return iterator.nextString(length.toInt()).also { sharedStorage.storeKey(it) }
    }

    override fun keyShortUnicode(): String {
        val byte = iterator.next()
        require(byte in KeyShortUnicode) { "Invalid token for short unicode key: ${byte.toUByte().toString(16)}" }

        val length = byte.toUByte() - KeyShortUnicode.offset.toUByte()
        return iterator.nextString(length.toInt()).also { sharedStorage.storeKey(it) }
    }

    override fun keyLongUnicode(): String {
        val firstByte = iterator.next()
        require(firstByte == KeyLongUnicode.firstByte) { "Invalid token for long unicode key: ${firstByte.toUByte().toString(16)}" }

        // TODO: move to shared method
        var counter = 0
        while (iterator.next() != SmileMarkers.STRING_END_MARKER) {
            counter++
        }

        iterator.rollback(counter + 1)

        val decoded = iterator.nextString(counter)
        require(iterator.next() == SmileMarkers.STRING_END_MARKER) { "Invalid end marker for long unicode key" }

        return decoded
    }
}
