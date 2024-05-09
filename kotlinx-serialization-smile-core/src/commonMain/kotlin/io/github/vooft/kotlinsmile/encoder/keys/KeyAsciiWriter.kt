package io.github.vooft.kotlinsmile.encoder.keys

import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.github.vooft.kotlinsmile.common.isAscii
import io.github.vooft.kotlinsmile.token.SmileKeyToken.KeyLongAscii
import io.github.vooft.kotlinsmile.token.SmileKeyToken.KeyShortAscii
import io.github.vooft.kotlinsmile.token.SmileMarkers

interface KeyAsciiWriter {
    fun keyShortAscii(value: String)
    fun keyLongAscii(value: String)
}

class KeyAsciiWriterSession(private val builder: ByteArrayBuilder) : KeyAsciiWriter {
    override fun keyShortAscii(value: String) {
        require(value.length in KeyShortAscii.BYTE_LENGTHS) { "Length must be in ${KeyShortAscii.BYTE_LENGTHS}, actual: ${value.length}" }
        require(value.isAscii()) { "Only ASCII characters are allowed, actual: $value" }

        builder.append(byte = value.length.toByte(), orMask = KeyShortAscii.mask)
        value.forEach { builder.append(it.code.toByte()) }
    }

    override fun keyLongAscii(value: String) {
        require(value.length in KeyLongAscii.BYTE_LENGTHS) { "Length must be in ${KeyLongAscii.BYTE_LENGTHS}, actual: ${value.length}" }
        require(value.isAscii()) { "Only ASCII characters are allowed, actual: $value" }

        builder.append(KeyLongAscii.mask)
        value.forEach { builder.append(it.code.toByte()) }
        builder.append(SmileMarkers.STRING_END_MARKER)
    }
}
