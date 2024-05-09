package io.github.vooft.kotlinsmile.encoder.values

import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.github.vooft.kotlinsmile.common.isAscii
import io.github.vooft.kotlinsmile.token.SmileValueToken.TinyAscii

interface TinyAsciiWriter {
    fun tinyAscii(value: String)
}

class TinyAsciiWriterSession(private val builder: ByteArrayBuilder) : TinyAsciiWriter {
    override fun tinyAscii(value: String) {
        require(value.length in TinyAscii.LENGTH_RANGE) { "Length must be in ${TinyAscii.LENGTH_RANGE}, actual: ${value.length}" }
        require(value.isAscii()) { "Only ASCII characters are allowed, actual: $value" }

        builder.append(byte = value.length.toByte(), offset = TinyAscii.offset)
        value.forEach { builder.append(it.code.toByte()) }
    }
}
