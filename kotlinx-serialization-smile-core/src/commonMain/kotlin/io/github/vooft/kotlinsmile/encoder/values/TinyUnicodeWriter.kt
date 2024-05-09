package io.github.vooft.kotlinsmile.encoder.values

import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.github.vooft.kotlinsmile.token.SmileValueToken.TinyUnicode

interface TinyUnicodeWriter {
    fun tinyUnicode(value: String)
}

class TinyUnicodeWriterSession(private val builder: ByteArrayBuilder): TinyUnicodeWriter {
    override fun tinyUnicode(value: String) {
        require(value.length < 32)
        builder.append(byte = value.length.toByte(), offset = TinyUnicode.offset)
        value.forEach { builder.append(it.code.toByte()) }
    }
}
