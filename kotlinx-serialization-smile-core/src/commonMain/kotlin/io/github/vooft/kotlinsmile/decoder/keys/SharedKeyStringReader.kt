package io.github.vooft.kotlinsmile.decoder.keys

import io.github.vooft.kotlinsmile.common.ByteArrayIterator
import io.github.vooft.kotlinsmile.common.shared.SmileSharedStorage
import io.github.vooft.kotlinsmile.token.SmileKeyToken.LongSharedKey
import io.github.vooft.kotlinsmile.token.SmileKeyToken.ShortSharedKey
import io.github.vooft.kotlinsmile.token.contains

interface SharedKeyStringReader {
    fun shortSharedKey(): String
    fun longSharedKey(): String
}

class SharedKeyStringReaderSession(
    private val iterator: ByteArrayIterator,
    private val sharedStorage: SmileSharedStorage
) : SharedKeyStringReader {
    override fun shortSharedKey(): String {
        val byte = iterator.next()
        require(byte in ShortSharedKey) { "Invalid token for short shared key: ${byte.toUByte().toString(16)}" }

        val id = byte.toUByte() - ShortSharedKey.offset.toUByte()
        return sharedStorage.getKeyById(id.toInt())
    }

    override fun longSharedKey(): String {
        val firstByte = iterator.next()
        require(firstByte in LongSharedKey) { "Invalid token for long shared key: ${firstByte.toUByte().toString(16)}" }

        val secondByte = iterator.next().toInt() and 0xFF

        var id = firstByte.toInt() and TWO_LSB_MASK
        id = id shl 8 or secondByte

        require(id in LongSharedKey.VALUES_RANGE) { "Invalid value for long shared key: $id, allowed: ${LongSharedKey.VALUES_RANGE}" }

        return sharedStorage.getKeyById(id)
    }
}

private const val TWO_LSB_MASK = 0b0000_0011
