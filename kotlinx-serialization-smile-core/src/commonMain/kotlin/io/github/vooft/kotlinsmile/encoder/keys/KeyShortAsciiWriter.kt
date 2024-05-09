package io.github.vooft.kotlinsmile.encoder.keys

import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.github.vooft.kotlinsmile.token.SmileKeyToken.ShortAsciiName

interface KeyShortAsciiWriter {
    fun keyShortAscii(name: String)
}

class KeyShortAsciiWriterSession(private val builder: ByteArrayBuilder) : KeyShortAsciiWriter {
    override fun keyShortAscii(name: String) {
        builder.append(byte = name.length.toByte(), offset = ShortAsciiName.offset)
        name.forEach { builder.append(it.code.toByte()) }
    }
}
