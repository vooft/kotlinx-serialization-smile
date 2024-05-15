package io.github.vooft.kotlinsmile.decoder.values

import io.github.vooft.kotlinsmile.common.ByteArrayIterator
import io.github.vooft.kotlinsmile.decoder.shared.DecodingSmileSharedStorage
import io.github.vooft.kotlinsmile.token.SmileKeyToken.LongSharedKey
import io.github.vooft.kotlinsmile.token.SmileKeyToken.ShortSharedKey
import io.github.vooft.kotlinsmile.token.contains

interface SharedStringReader {
    fun shortSharedKey(): String
    fun longSharedKey(): String
}

class SharedStringReaderSession(
    private val iterator: ByteArrayIterator,
    private val sharedStorage: DecodingSmileSharedStorage
) : SharedStringReader {
    override fun shortSharedKey(): String {
        val byte = iterator.next()
        require(byte in ShortSharedKey) { "Invalid token for short shared key: ${byte.toUByte().toString(16)}" }

        val id = byte.toUByte() - ShortSharedKey.offset.toUByte()
        return sharedStorage.getKey(id.toInt())
    }

    override fun longSharedKey(): String {
        val byte = iterator.next()
        require(byte in LongSharedKey) { "Invalid token for long shared key: ${byte.toUByte().toString(16)}" }

        var id = byte.toInt() and 0xFF and TWO_LSB_MASK
        id = id shl 8
        id = id or (iterator.next().toInt() and 0xFF)

        require(id in LongSharedKey.VALUES_RANGE) { "Invalid value for long shared key: $id, allowed: ${LongSharedKey.VALUES_RANGE}" }

        return sharedStorage.getKey(id)
    }
}

private const val TWO_LSB_MASK = 0b0000_0011
