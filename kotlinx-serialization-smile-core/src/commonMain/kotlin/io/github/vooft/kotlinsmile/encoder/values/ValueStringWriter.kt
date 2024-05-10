package io.github.vooft.kotlinsmile.encoder.values

import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.github.vooft.kotlinsmile.common.SmileString
import io.github.vooft.kotlinsmile.common.length
import io.github.vooft.kotlinsmile.common.requireAscii
import io.github.vooft.kotlinsmile.common.requireLength
import io.github.vooft.kotlinsmile.common.requireUnicode
import io.github.vooft.kotlinsmile.token.SmileValueToken.TinyAscii
import io.github.vooft.kotlinsmile.token.SmileValueToken.TinyUnicode

interface ValueStringWriter {
    fun valueTinyAscii(value: SmileString)
    fun valueTinyUnicode(value: SmileString)
}

class ValueStringWriterSession(private val builder: ByteArrayBuilder) : ValueStringWriter {
    override fun valueTinyAscii(value: SmileString) {
        value.requireLength(TinyAscii.BYTE_LENGTHS)
        value.requireAscii()

        // length starts with 1, because empty string is a separate token
        val writtenLength = value.length - 1
        builder.append(byte = writtenLength.toByte(), orMask = TinyAscii.mask)
        builder.append(value.encoded)
    }

    override fun valueTinyUnicode(value: SmileString) {
        value.requireLength(TinyUnicode.BYTE_LENGTHS)
        value.requireUnicode()

        // length starts with 2, because empty string is a separate token and 1 byte is ascii anyways
        val writtenLength = value.length - 2
        builder.append(byte = writtenLength.toByte(), orMask = TinyUnicode.mask)
        builder.append(value.encoded)
    }
}
