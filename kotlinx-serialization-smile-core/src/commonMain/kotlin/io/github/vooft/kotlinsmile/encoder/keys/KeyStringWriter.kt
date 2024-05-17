package io.github.vooft.kotlinsmile.encoder.keys

import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.github.vooft.kotlinsmile.common.SmileString
import io.github.vooft.kotlinsmile.common.length
import io.github.vooft.kotlinsmile.common.requireAscii
import io.github.vooft.kotlinsmile.common.requireLength
import io.github.vooft.kotlinsmile.common.requireUnicode
import io.github.vooft.kotlinsmile.common.shared.SmileSharedStorage
import io.github.vooft.kotlinsmile.token.SmileKeyToken.KeyLongUnicode
import io.github.vooft.kotlinsmile.token.SmileKeyToken.KeyShortAscii
import io.github.vooft.kotlinsmile.token.SmileKeyToken.KeyShortUnicode
import io.github.vooft.kotlinsmile.token.SmileMarkers

interface KeyStringWriter {
    fun keyShortAscii(value: SmileString)
    fun keyShortUnicode(value: SmileString)
    fun keyLongUnicode(value: SmileString)
}

class KeyStringWriterSession(
    private val builder: ByteArrayBuilder,
    private val sharedStorage: SmileSharedStorage
) : KeyStringWriter {
    override fun keyShortAscii(value: SmileString) {
        value.requireLength(KeyShortAscii.BYTE_LENGTHS)
        value.requireAscii()

        sharedStorage.storeKey(value.value)

        builder.append(byte = value.length.toByte(), offset = KeyShortAscii.offset)
        builder.append(value.encoded)
    }

    override fun keyShortUnicode(value: SmileString) {
        value.requireUnicode()
        value.requireLength(KeyShortUnicode.BYTE_LENGTHS)

        sharedStorage.storeKey(value.value)

        builder.append(byte = value.length.toByte(), offset = KeyShortUnicode.offset)
        builder.append(value.encoded)
    }

    override fun keyLongUnicode(value: SmileString) {
        value.requireLength(KeyLongUnicode.BYTE_LENGTHS)

        sharedStorage.storeKey(value.value)

        builder.append(KeyLongUnicode.firstByte)
        builder.append(value.encoded)
        builder.append(SmileMarkers.STRING_END_MARKER)
    }
}
