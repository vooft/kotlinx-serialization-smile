package io.github.vooft.kotlinsmile.decoder.values

import io.github.vooft.kotlinsmile.common.ByteArrayIterator
import io.github.vooft.kotlinsmile.token.SmileValueToken.DoubleValue
import io.github.vooft.kotlinsmile.token.SmileValueToken.FloatValue

interface FloatReader {
    fun valueFloat(): Float
    fun valueDouble(): Double
}

class FloatReaderSession(private val iterator: ByteArrayIterator) : FloatReader {
    override fun valueFloat(): Float {
        val firstByte = iterator.next()
        require(firstByte == FloatValue.firstByte) {
            "Invalid token for float value ${FloatValue.firstByte}, actual: $firstByte"
        }

        var bits = iterator.next().toInt()
        repeat(FloatValue.SERIALIZED_BYTES - 1) {
            bits = bits shl 7
            bits = bits or iterator.next().toInt()
        }

        return Float.fromBits(bits)
    }

    override fun valueDouble(): Double {
        val firstByte = iterator.next()
        require(firstByte == DoubleValue.firstByte) {
            "Invalid token for double value ${DoubleValue.firstByte}, actual: $firstByte"
        }

        var bits = iterator.next().toLong()
        repeat(DoubleValue.SERIALIZED_BYTES - 1) {
            bits = bits shl 7
            bits = bits or iterator.next().toLong()
        }

        return Double.fromBits(bits)
    }
}
