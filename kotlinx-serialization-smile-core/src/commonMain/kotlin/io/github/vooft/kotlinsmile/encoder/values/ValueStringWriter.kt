package io.github.vooft.kotlinsmile.encoder.values

import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.github.vooft.kotlinsmile.common.SmileString
import io.github.vooft.kotlinsmile.common.length
import io.github.vooft.kotlinsmile.common.requireAscii
import io.github.vooft.kotlinsmile.common.requireLength
import io.github.vooft.kotlinsmile.common.requireUnicode
import io.github.vooft.kotlinsmile.token.SmileValueToken.ShortAscii
import io.github.vooft.kotlinsmile.token.SmileValueToken.ShortUnicode
import io.github.vooft.kotlinsmile.token.SmileValueToken.TinyAscii
import io.github.vooft.kotlinsmile.token.SmileValueToken.TinyUnicode

interface ValueStringWriter {
    fun valueTinyAscii(value: SmileString)
    fun valueShortAscii(value: SmileString)
    fun valueTinyUnicode(value: SmileString)
    fun valueShortUnicode(value: SmileString)
}

class ValueStringWriterSession(private val builder: ByteArrayBuilder) : ValueStringWriter {
    override fun valueTinyAscii(value: SmileString) {
        value.requireLength(TinyAscii.lengths)
        value.requireAscii()

        val writtenLength = value.length - TinyAscii.lengths.first
        builder.append(byte = writtenLength.toByte(), orMask = TinyAscii.mask)
        builder.append(value.encoded)
    }

    override fun valueShortAscii(value: SmileString) {
        value.requireLength(ShortAscii.lengths)
        value.requireAscii()

        val writtenLength = value.length - ShortAscii.lengths.first
        builder.append(byte = writtenLength.toByte(), orMask = ShortAscii.mask)
        builder.append(value.encoded)
    }

    override fun valueTinyUnicode(value: SmileString) {
        value.requireLength(TinyUnicode.lengths)
        value.requireUnicode()

        val writtenLength = value.length - TinyUnicode.lengths.first
        builder.append(byte = writtenLength.toByte(), orMask = TinyUnicode.mask)
        builder.append(value.encoded)
    }

    override fun valueShortUnicode(value: SmileString) {
        value.requireLength(ShortUnicode.lengths)
        value.requireUnicode()

        val writtenLength = value.length - ShortUnicode.lengths.first
        builder.append(byte = writtenLength.toByte(), orMask = ShortUnicode.mask)
        builder.append(value.encoded)
    }
}
