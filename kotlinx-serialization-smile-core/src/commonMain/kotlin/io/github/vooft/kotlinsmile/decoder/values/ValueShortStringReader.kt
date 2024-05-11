package io.github.vooft.kotlinsmile.decoder.values

import io.github.vooft.kotlinsmile.common.ByteArrayIterator
import io.github.vooft.kotlinsmile.token.SmileValueToken.ShortAscii
import io.github.vooft.kotlinsmile.token.SmileValueToken.ShortUnicode
import io.github.vooft.kotlinsmile.token.SmileValueToken.SmileValueShortStringToken
import io.github.vooft.kotlinsmile.token.SmileValueToken.TinyAscii
import io.github.vooft.kotlinsmile.token.SmileValueToken.TinyUnicode

interface ValueShortStringReader {
    fun valueTinyAscii(): String
    fun valueShortAscii(): String
    fun valueTinyUnicode(): String
    fun valueShortUnicode(): String
}

class ValueShortStringReaderSession(private val iterator: ByteArrayIterator): ValueShortStringReader {
    override fun valueTinyAscii()= TinyAscii.readString()
    override fun valueShortAscii() = ShortAscii.readString()
    override fun valueTinyUnicode() = TinyUnicode.readString()
    override fun valueShortUnicode() = ShortUnicode.readString()

    private fun SmileValueShortStringToken.readString(): String {
        val writtenLength = iterator.next().toUByte() - offset.toUByte()
        val length = writtenLength.toInt() + lengths.first
        val encoded = iterator.next(length)
        return encoded.decodeToString()
    }
}
