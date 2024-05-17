package io.github.vooft.kotlinsmile.decoder.values

import io.github.vooft.kotlinsmile.common.ByteArrayIterator
import io.github.vooft.kotlinsmile.common.shared.SmileSharedStorage
import io.github.vooft.kotlinsmile.token.SmileValueToken.ShortAscii
import io.github.vooft.kotlinsmile.token.SmileValueToken.ShortUnicode
import io.github.vooft.kotlinsmile.token.SmileValueToken.SmileValueShortStringToken
import io.github.vooft.kotlinsmile.token.SmileValueToken.TinyAscii
import io.github.vooft.kotlinsmile.token.SmileValueToken.TinyUnicode
import io.github.vooft.kotlinsmile.token.contains

interface ValueShortStringReader {
    fun valueTinyAscii(): String
    fun valueShortAscii(): String
    fun valueTinyUnicode(): String
    fun valueShortUnicode(): String
}

class ValueShortStringReaderSession(
    private val iterator: ByteArrayIterator,
    private val sharedStorage: SmileSharedStorage
) : ValueShortStringReader {

    override fun valueTinyAscii() = TinyAscii.readString()
    override fun valueShortAscii() = ShortAscii.readString()
    override fun valueTinyUnicode() = TinyUnicode.readString()
    override fun valueShortUnicode() = ShortUnicode.readString()

    private fun SmileValueShortStringToken.readString(): String {
        val byte = iterator.next()
        require(byte in this) { "Invalid token for short string: ${byte.toUByte().toString(16)}" }

        val writtenLength = byte.toUByte() - offset.toUByte()
        val length = writtenLength.toInt() + lengths.first
        val decoded = iterator.nextString(length)
        return decoded.also { sharedStorage.storeValue(it) }
    }
}
