package io.github.vooft.kotlinsmile.decoder.raw

import io.github.vooft.kotlinsmile.common.ByteArrayIterator

fun ByteArrayIterator.nextRawBinary(decodedLength: Int): ByteArray {
    var buffer = 0
    var carryoverBits = -1
    var index = 0

    val result = ByteArray(decodedLength)

    do {
        val byte = next().toInt() and 0xFF
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

    val byte = next().toInt() and 0xFF
    buffer = buffer shr 7
    buffer = buffer shl (8 - carryoverBits - 1)
    result[index] = (buffer or byte and BYTE_FF).toByte()

    return result
}

private const val BYTE_FF = 0xFF
