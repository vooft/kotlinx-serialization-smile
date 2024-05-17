package io.github.vooft.kotlinsmile.encoder.values

import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.github.vooft.kotlinsmile.common.shared.SmileSharedStorage
import io.github.vooft.kotlinsmile.token.SmileValueToken.LongSharedValue
import io.github.vooft.kotlinsmile.token.SmileValueToken.ShortSharedValue
import kotlin.experimental.or

interface SharedValueShortStringWriter {
    fun valueShared(key: String)
    fun hasValue(key: String): Boolean
}

class SharedValueShortStringWriterSession(
    private val builder: ByteArrayBuilder,
    private val sharedStorage: SmileSharedStorage
) : SharedValueShortStringWriter {

    override fun valueShared(key: String) {
        val index = sharedStorage.lookupValue(key)
        requireNotNull(index) { "Value $key is not shared yet" }

        if (index !in LongSharedValue.VALUES_RANGE) {
            val id = ShortSharedValue.offset.toUByte() + index.toUByte()
            builder.append(id.toByte())
        } else {
            val mostSignificantBits = index shr 8 and TWO_LSB_MASK
            builder.append(LongSharedValue.firstByte or mostSignificantBits.toByte())
            builder.append((index and 0xFF).toByte())
        }
    }

    override fun hasValue(key: String): Boolean {
        return sharedStorage.lookupValue(key) != null
    }
}

private const val TWO_LSB_MASK = 0b0000_0011
