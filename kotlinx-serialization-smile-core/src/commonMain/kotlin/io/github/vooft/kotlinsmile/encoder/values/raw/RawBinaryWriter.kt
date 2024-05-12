package io.github.vooft.kotlinsmile.encoder.values.raw

import io.github.vooft.kotlinsmile.common.ByteArrayBuilder

fun ByteArrayBuilder.appendRawBinary(data: ByteArray) {
    var buffer = 0u
    var index = 0

    while (index < data.size) {
        var carryoverBits = 0

        while (carryoverBits < 7) {
            carryoverBits++

            // read byte and zero whatever is not important
            val byte = data[index++].toUInt() and BYTE_FF

            // smallest byte should be zero, just copy from the just read one
            buffer = buffer or byte
            println("B: ${buffer.toString(2).padStart(16, '0')}")

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
            buffer = 0u
        }
    }
}

private val BYTE_FF = 0xFFu
private val BYTE_7_BIT = 0b0111_1111u

private fun ByteArray.toHexString() = joinToString(", ", "[", "]") { it.toUByte().toString(16).padStart(2, '0') } + "]"
private fun ByteArray.toBinaryString() = joinToString(", ", "[", "]") { it.toUByte().toString(2).padStart(8, '0') }
