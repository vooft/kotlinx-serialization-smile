package io.github.vooft.kotlinsmile.decoder.keys

import io.github.vooft.kotlinsmile.common.ByteArrayIterator
import io.github.vooft.kotlinsmile.token.SmileKeyToken.KeyShortAscii
import io.github.vooft.kotlinsmile.token.SmileKeyToken.KeyShortUnicode
import io.github.vooft.kotlinsmile.token.contains
import kotlin.experimental.xor

interface KeyStringReader {
    fun keyShortAscii(): String
    fun keyShortUnicode(): String
    fun keyLongUnicode(): String
}

class KeyStringReaderSession(private val iterator: ByteArrayIterator): KeyStringReader {
    override fun keyShortAscii(): String {
        val byte = iterator.next()
        require(byte in KeyShortAscii) { "Invalid token for short ascii key: ${byte.toUByte().toString(16)}" }

        val length = byte.toUByte() - KeyShortAscii.offset.toUByte()
        val encoded = iterator.next(length.toInt())
        return encoded.decodeToString()
    }

    override fun keyShortUnicode(): String {
        val byte = iterator.next()
        require(byte in KeyShortUnicode) { "Invalid token for short unicode key: ${byte.toUByte().toString(16)}" }

        val length = (byte xor KeyShortUnicode.offset).toInt()
        val encoded = iterator.next(length)
        return encoded.decodeToString()
    }

    override fun keyLongUnicode(): String {
        TODO()
    }
}
