package io.github.vooft.kotlinsmile.encoder.keys

import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.github.vooft.kotlinsmile.smile.ShortAsciiName

interface KeyShortAsciiWriter {
    fun keyShortAscii(name: String)
}

class KeyShortAsciiWriterSession(private val builder: ByteArrayBuilder) : KeyShortAsciiWriter {
    override fun keyShortAscii(name: String) {
        builder.append((name.length + ShortAsciiName.offset).toByte())
        name.forEach { builder.append(it.code.toByte()) }
    }
}
