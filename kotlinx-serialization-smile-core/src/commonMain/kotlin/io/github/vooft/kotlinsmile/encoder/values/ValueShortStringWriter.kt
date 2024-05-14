package io.github.vooft.kotlinsmile.encoder.values

import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.github.vooft.kotlinsmile.common.SmileString
import io.github.vooft.kotlinsmile.common.length
import io.github.vooft.kotlinsmile.common.requireAscii
import io.github.vooft.kotlinsmile.common.requireLength
import io.github.vooft.kotlinsmile.common.requireUnicode
import io.github.vooft.kotlinsmile.token.SmileValueToken.ShortAscii
import io.github.vooft.kotlinsmile.token.SmileValueToken.ShortUnicode
import io.github.vooft.kotlinsmile.token.SmileValueToken.SmileValueShortStringToken
import io.github.vooft.kotlinsmile.token.SmileValueToken.TinyAscii
import io.github.vooft.kotlinsmile.token.SmileValueToken.TinyUnicode

interface ValueShortStringWriter {
    fun valueTinyAscii(value: SmileString)
    fun valueShortAscii(value: SmileString)
    fun valueTinyUnicode(value: SmileString)
    fun valueShortUnicode(value: SmileString)
}

class ValueShortStringWriterSession(private val builder: ByteArrayBuilder) : ValueShortStringWriter {

    override fun valueTinyAscii(value: SmileString) = TinyAscii.append(value)
    override fun valueShortAscii(value: SmileString) = ShortAscii.append(value)
    override fun valueTinyUnicode(value: SmileString) = TinyUnicode.append(value)
    override fun valueShortUnicode(value: SmileString) = ShortUnicode.append(value)

    private inline fun SmileValueShortStringToken.append(value: SmileString) {
        value.requireLength(lengths)
        if (isUnicode) {
            value.requireUnicode()
        } else {
            value.requireAscii()
        }

        val writtenLength = value.length - lengths.first
        builder.append(byte = writtenLength.toByte(), offset = offset)
        builder.append(value.encoded)
    }
}
