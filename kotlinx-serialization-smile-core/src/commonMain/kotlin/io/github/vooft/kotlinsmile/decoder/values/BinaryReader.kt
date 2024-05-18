package io.github.vooft.kotlinsmile.decoder.values

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.vooft.kotlinsmile.common.ByteArrayIterator
import io.github.vooft.kotlinsmile.decoder.raw.nextRawBinary
import io.github.vooft.kotlinsmile.decoder.raw.nextRawInt
import io.github.vooft.kotlinsmile.token.SmileValueToken.BinaryValue

interface BinaryReader {
    fun valueBinary(): ByteArray
}

class BinaryReaderSession(private val iterator: ByteArrayIterator) : BinaryReader {
    override fun valueBinary(): ByteArray {
        val firstByte = iterator.next()
        require(firstByte == BinaryValue.firstByte) {
            "Invalid token for binary value ${BinaryValue.firstByte}, actual: $firstByte"
        }

        val decodedLength = iterator.nextRawInt()
        logger.debug { "Decoding byte array with length $decodedLength" }

        return iterator.nextRawBinary(decodedLength = decodedLength)
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
