package io.github.vooft.kotlinsmile.encoder.keys

import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.github.vooft.kotlinsmile.token.SmileKeyToken.KeyShortAscii

interface KeyShortUnicodeWriter {
    fun keyShortUnicode(value: String)
}

class KeyShortUnicodeWriterSession(private val builder: ByteArrayBuilder) : KeyShortUnicodeWriter {
    override fun keyShortUnicode(value: String) {
        builder.append(byte = value.length.toByte(), offset = KeyShortAscii.offset)
        value.forEach { builder.append(it.code.toByte()) }
    }
}
