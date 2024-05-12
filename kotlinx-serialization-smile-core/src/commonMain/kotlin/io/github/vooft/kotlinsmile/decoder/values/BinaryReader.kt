package io.github.vooft.kotlinsmile.decoder.values

import io.github.vooft.kotlinsmile.adapter.decoder.common.peekValueToken
import io.github.vooft.kotlinsmile.common.ByteArrayIterator
import io.github.vooft.kotlinsmile.common.ZigzagInteger
import io.github.vooft.kotlinsmile.decoder.values.raw.nextRawBinary
import io.github.vooft.kotlinsmile.token.SmileValueToken.BinaryValue
import io.github.vooft.kotlinsmile.token.SmileValueToken.RegularInteger
import io.github.vooft.kotlinsmile.token.SmileValueToken.SmallInteger

interface BinaryReader {
    fun valueBinary(): ByteArray
}

class BinaryReaderSession(private val iterator: ByteArrayIterator): BinaryReader {
    override fun valueBinary(): ByteArray {
        val firstByte = iterator.next()
        require(firstByte == BinaryValue.firstByte) {
            "Invalid token for binary value ${BinaryValue.firstByte}, actual: $firstByte"
        }

        val integerReader = IntegerReaderSession(iterator)
        val zigzagLength = when (val token = iterator.peekValueToken()) {
            SmallInteger -> integerReader.valueSmallInteger().toInt()
            RegularInteger -> integerReader.valueRegularInteger()
            else -> error("Unexpected token $token")
        }

        val actualLength = ZigzagInteger.decode(zigzagLength)
        return iterator.nextRawBinary(actualLength)
    }
}
