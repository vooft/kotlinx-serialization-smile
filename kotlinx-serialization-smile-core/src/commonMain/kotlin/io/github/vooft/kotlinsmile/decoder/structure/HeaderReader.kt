package io.github.vooft.kotlinsmile.decoder.structure

import io.github.vooft.kotlinsmile.SmileConfig
import io.github.vooft.kotlinsmile.common.ByteArrayIterator
import io.github.vooft.kotlinsmile.token.SmileMarkers.FIXED_HEADER
import io.github.vooft.kotlinsmile.token.SmileMarkers.SHARED_STRING_PROPERTY_NAME_MASK
import io.github.vooft.kotlinsmile.token.SmileMarkers.SHARED_STRING_VALUE_MASK
import kotlin.experimental.and

interface HeaderReader {
    fun header(): SmileConfig
}

class HeaderReaderSession(private val iterator: ByteArrayIterator) : HeaderReader {
    override fun header(): SmileConfig {
        val fixedHeader = iterator.nextByteArray(3)
        require(fixedHeader.contentEquals(FIXED_HEADER)) { "Invalid header" }

        val configByte = iterator.next()
        return SmileConfig(
            sharePropertyName = configByte and SHARED_STRING_PROPERTY_NAME_MASK != 0.toByte(),
            shareStringValue = configByte and SHARED_STRING_VALUE_MASK != 0.toByte()
        )
    }
}
