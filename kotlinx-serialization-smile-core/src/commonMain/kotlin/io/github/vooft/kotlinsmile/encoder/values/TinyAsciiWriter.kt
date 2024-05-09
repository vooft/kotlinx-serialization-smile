package io.github.vooft.kotlinsmile.encoder.values

import io.github.vooft.kotlinsmile.smile.TinyUnicode
import kotlinx.io.bytestring.ByteStringBuilder
import kotlin.experimental.or

interface TinyAsciiWriter {
    fun tinyAscii(value: String)
}

class TinyAsciiWriterSession(private val builder: ByteStringBuilder): TinyAsciiWriter {
    override fun tinyAscii(value: String) {
        require(value.length < 32)
        builder.append(TinyUnicode.mask or value.length.toByte())
        value.forEach { builder.append(it.code.toByte()) }
    }
}
