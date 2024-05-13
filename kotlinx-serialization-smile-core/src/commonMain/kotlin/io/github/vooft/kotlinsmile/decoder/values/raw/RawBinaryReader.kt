package io.github.vooft.kotlinsmile.decoder.values.raw

import io.github.vooft.kotlinsmile.adapter.decoder.common.nextUByte
import io.github.vooft.kotlinsmile.common.ByteArrayIterator

fun ByteArrayIterator.nextRawBinary(decodedLength: Int): ByteArray {
    var buffer = 0u
    var carryoverBits = -1
    var index = 0

    val result = ByteArray(decodedLength)

    do {
        val byte = nextUByte()
        buffer = buffer or byte

        if (carryoverBits >= 0) {
            val toWrite = buffer shr carryoverBits and BYTE_FF
            result[index++] = toWrite.toByte()

            val mask = BYTE_FF shr (8 - carryoverBits)
            buffer = buffer and mask
        } else {
            carryoverBits = 7
        }

        buffer = buffer shl 7
        carryoverBits--

        // if carryoverBits is less than 0, then we need to start reading another byte first
    } while (index < decodedLength - 1 || carryoverBits < 0)

//    if (carryoverBits < 0) {
//        buffer = nextUByte() shl 7
//        carryoverBits = 6
//    }

    val byte = nextUByte()
    buffer = buffer shr 7
    buffer = buffer shl (8 - carryoverBits - 1)
    result[index] = (buffer or byte and BYTE_FF).toByte()

    return result
}

private const val BYTE_FF = 0xFFu
