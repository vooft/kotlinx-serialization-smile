package io.github.vooft.kotlinsmile.decoder.values.raw

import io.github.vooft.kotlinsmile.adapter.decoder.common.nextUByte
import io.github.vooft.kotlinsmile.common.ByteArrayIterator
import io.github.vooft.kotlinsmile.common.buildByteArray

fun ByteArrayIterator.nextRawBinary(encodedLength: Int): ByteArray {
    return buildByteArray {
        var buffer = 0u
        var carryoverBits = -1
        var index = 0

        do {
            val byte = nextUByte()
            buffer = buffer or byte

            if (carryoverBits >= 0) {
                val toWrite = buffer shr carryoverBits and BYTE_FF
                append(toWrite.toByte())

                val mask = BYTE_FF shr (8 - carryoverBits)
                buffer = buffer and mask
            } else {
                carryoverBits = 7
            }

            buffer = buffer shl 7
            carryoverBits--
            index++
        } while (index < encodedLength - 1)

        val byte = nextUByte()
        buffer = buffer shr 7
        buffer = buffer shl (8 - carryoverBits - 1)
        append((buffer or byte and BYTE_FF).toByte())
    }
}

private val BYTE_FF = 0xFFu
private val BYTE_7_BIT = 0b0111_1111u
