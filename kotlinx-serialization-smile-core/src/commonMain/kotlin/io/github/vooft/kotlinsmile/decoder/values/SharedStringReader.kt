package io.github.vooft.kotlinsmile.decoder.values

import io.github.vooft.kotlinsmile.common.ByteArrayIterator
import io.github.vooft.kotlinsmile.decoder.shared.DecodingSmileSharedStorage
import io.github.vooft.kotlinsmile.token.SmileKeyToken.ShortSharedKey
import io.github.vooft.kotlinsmile.token.contains

interface SharedStringReader {
    fun shortSharedKey(): String
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
}
