package io.github.vooft.kotlinsmile.encoder.keys

import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.github.vooft.kotlinsmile.common.isAscii
import io.github.vooft.kotlinsmile.common.isUnicode
import io.github.vooft.kotlinsmile.token.SmileKeyToken.KeyLongUnicode
import io.github.vooft.kotlinsmile.token.SmileKeyToken.KeyShortAscii
import io.github.vooft.kotlinsmile.token.SmileKeyToken.KeyShortUnicode
import io.github.vooft.kotlinsmile.token.SmileMarkers

interface KeyStringWriter {
    fun keyShortAscii(value: String)
    fun keyShortUnicode(value: String)
    fun keyLongUnicode(value: String)
}

class KeyStringWriterSession(private val builder: ByteArrayBuilder) : KeyStringWriter {
    override fun keyShortAscii(value: String) {
        require(value.length in KeyShortAscii.BYTE_LENGTHS) { "Length must be in ${KeyShortAscii.BYTE_LENGTHS}, actual: ${value.length}" }
        require(value.isAscii()) { "Only ASCII characters are allowed, actual: $value" }

        builder.append(byte = value.length.toByte(), orMask = KeyShortAscii.mask)
        builder.append(value.encodeToByteArray()) // utf-8 with only ascii chars
    }

    override fun keyShortUnicode(value: String) {
        require(value.isUnicode()) { "For only-ASCII strings need to use different method, actual: $value" }

        val bytes = value.encodeToByteArray()
        require(bytes.size in KeyShortUnicode.BYTE_LENGTHS) { "Length must be in ${KeyShortUnicode.BYTE_LENGTHS}, actual: ${bytes.size}" }

        builder.append(byte = bytes.size.toByte(), orMask = KeyShortUnicode.mask)
        builder.append(bytes)
    }

    override fun keyLongUnicode(value: String) {
        val bytes = value.encodeToByteArray()
        require(bytes.size in KeyLongUnicode.BYTE_LENGTHS) { "Length must be in ${KeyLongUnicode.BYTE_LENGTHS}, actual: ${bytes.size}" }

        builder.append(KeyLongUnicode.mask)
        builder.append(value.encodeToByteArray())
        builder.append(SmileMarkers.STRING_END_MARKER)
    }
}
