package io.github.vooft.kotlinsmile.decoder.keys

import io.github.vooft.kotlinsmile.common.ByteArrayIterator
import io.github.vooft.kotlinsmile.common.shared.SmileSharedStorage
import io.github.vooft.kotlinsmile.decoder.raw.nextRawLongString
import io.github.vooft.kotlinsmile.token.SmileKeyToken.KeyLongUnicode
import io.github.vooft.kotlinsmile.token.SmileKeyToken.KeyShortAscii
import io.github.vooft.kotlinsmile.token.SmileKeyToken.KeyShortUnicode
import io.github.vooft.kotlinsmile.token.contains

interface KeyStringReader {
    fun keyShortAscii(): String
    fun keyShortUnicode(): String
    fun keyLongUnicode(): String
}

class KeyStringReaderSession(
    private val iterator: ByteArrayIterator,
    private val sharedStorage: SmileSharedStorage
) : KeyStringReader {
    override fun keyShortAscii(): String {
        val byte = iterator.next()
        require(byte in KeyShortAscii) { "Invalid token for short ascii key: ${byte.toUByte().toString(16)}" }

        val length = byte.toUByte() - KeyShortAscii.offset.toUByte()
        val result = iterator.nextString(length.toInt())
        return result.also { sharedStorage.storeKey(it) }
    }

    override fun keyShortUnicode(): String {
        val byte = iterator.next()
        require(byte in KeyShortUnicode) { "Invalid token for short unicode key: ${byte.toUByte().toString(16)}" }

        val length = byte.toUByte() - KeyShortUnicode.offset.toUByte()
        val result = iterator.nextString(length.toInt())
        return result.also { sharedStorage.storeKey(it) }
    }

    override fun keyLongUnicode(): String {
        val firstByte = iterator.next()
        require(firstByte == KeyLongUnicode.firstByte) { "Invalid token for long unicode key: ${firstByte.toUByte().toString(16)}" }

        return iterator.nextRawLongString().also { sharedStorage.storeKey(it) }
    }
}
