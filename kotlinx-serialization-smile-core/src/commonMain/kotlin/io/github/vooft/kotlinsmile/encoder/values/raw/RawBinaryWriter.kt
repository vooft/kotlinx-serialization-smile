package io.github.vooft.kotlinsmile.encoder.values.raw

import io.github.vooft.kotlinsmile.common.ByteArrayBuilder

fun ByteArrayBuilder.appendRawBinary(data: ByteArray) {
    var buffer = 0
    var index = 0

    while (index < data.size) {
        var carryoverBits = 0

        while (carryoverBits < 7) {
            carryoverBits++

            // read byte and zero whatever is not important
            val byte = data[index++].toInt() and BYTE_FF

            // smallest byte should be zero, just copy from the just read one
            buffer = buffer or byte

            // shift right to make sure all carryover MSB are written
            append((buffer shr carryoverBits and BYTE_7_BIT).toByte())

            // create mask and zero out previously carryover bits
            val mask = BYTE_FF shr (8 - carryoverBits)
            buffer = buffer and mask

            // shift left the remaining to become carryover
            buffer = buffer shl 8 // 0 0 carryover 0

            // exit if there is no more bytes to read
            if (index == data.size) {
                break
            }
        }

        if (carryoverBits > 0) {
            append((buffer shr 8).toByte())
            buffer = 0
        }
    }
}

private const val BYTE_FF: Int = 0xFF
private const val BYTE_7_BIT: Int = 0b0111_1111
