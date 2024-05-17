package io.github.vooft.kotlinsmile.encoder.keys

import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.github.vooft.kotlinsmile.encoder.shared.EncodingSmileSharedStorage
import io.github.vooft.kotlinsmile.token.SmileKeyToken.LongSharedKey
import io.github.vooft.kotlinsmile.token.SmileKeyToken.ShortSharedKey
import kotlin.experimental.or

interface SharedKeyStringWriter {
    fun keyShared(key: String)
    fun hasKey(key: String): Boolean
}

class SharedKeyStringWriterSession(
    private val builder: ByteArrayBuilder,
    private val sharedStorage: EncodingSmileSharedStorage
) : SharedKeyStringWriter {

    override fun keyShared(key: String) {
        val index = sharedStorage.lookupKey(key)
        requireNotNull(index) { "Key $key is not shared yet" }

        if (index < LONG_REFERENCE_FIRST) {
            val id = ShortSharedKey.offset.toUByte() + index.toUByte()
            builder.append(id.toByte())
        } else {
            val mostSignificantBits = index shr 8 and TWO_LSB_MASK
            builder.append(LongSharedKey.firstByte or mostSignificantBits.toByte())
            builder.append((index and 0xFF).toByte())
        }
    }

    override fun hasKey(key: String): Boolean {
        return sharedStorage.lookupKey(key) != null
    }
}

private const val LONG_REFERENCE_FIRST = 31
private const val TWO_LSB_MASK = 0b0000_0011
