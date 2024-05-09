package io.github.vooft.kotlinsmile.encoder.values

import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.github.vooft.kotlinsmile.token.SmileValueToken

interface TinyAsciiWriter {
    fun tinyAscii(value: String)
}

class TinyAsciiWriterSession(private val builder: ByteArrayBuilder): TinyAsciiWriter {
    override fun tinyAscii(value: String) {
        require(value.length < 32)
        builder.append(byte = value.length.toByte(), offset = SmileValueToken.TinyAscii.offset)
        value.forEach { builder.append(it.code.toByte()) }
    }
}
