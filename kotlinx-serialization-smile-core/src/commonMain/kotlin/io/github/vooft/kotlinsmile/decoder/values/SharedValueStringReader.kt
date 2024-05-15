package io.github.vooft.kotlinsmile.decoder.values

import io.github.vooft.kotlinsmile.common.ByteArrayIterator
import io.github.vooft.kotlinsmile.decoder.shared.DecodingSmileSharedStorage
import io.github.vooft.kotlinsmile.token.SmileValueToken.LongSharedValue
import io.github.vooft.kotlinsmile.token.SmileValueToken.ShortSharedValue
import io.github.vooft.kotlinsmile.token.contains

interface SharedValueStringReader {
    fun shortSharedValue(): String
    fun longSharedValue(): String
}

class SharedValueStringReaderSession(
    private val iterator: ByteArrayIterator,
    private val sharedStorage: DecodingSmileSharedStorage
) : SharedValueStringReader {

    override fun shortSharedValue(): String {
        val byte = iterator.next()
        require(byte in ShortSharedValue) { "Invalid token for short shared value: ${byte.toUByte().toString(16)}" }

        if (byte == 0.toByte()) {
            throw InvalidSmileSpecImplementationException("Current Smile spec doesn't allow shared string index 0")
        }

        val id = byte.toUByte() - ShortSharedValue.offset.toUByte()
        return sharedStorage.getValue(id.toInt())
    }

    override fun longSharedValue(): String {
        val firstByte = iterator.next()
        require(firstByte in LongSharedValue) { "Invalid token for long shared value: ${firstByte.toUByte().toString(16)}" }

        val secondByte = iterator.next().toInt() and 0xFF

        var id = firstByte.toInt() and TWO_LSB_MASK
        id = id shl 8 or secondByte

        require(id in LongSharedValue.VALUES_RANGE) { "Invalid value for long shared value: $id, allowed: ${LongSharedValue.VALUES_RANGE}" }

        return sharedStorage.getValue(id + LongSharedValue.idOffset)
    }
}

class InvalidSmileSpecImplementationException(message: String) : RuntimeException(message)

private const val TWO_LSB_MASK = 0b0000_0011
