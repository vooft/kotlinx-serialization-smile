package io.github.vooft.kotlinsmile.decoder.raw

import io.github.vooft.kotlinsmile.common.ByteArrayIterator
import kotlin.experimental.and
import kotlin.experimental.xor

fun ByteArrayIterator.nextRawInt(): Int {
    var result = 0
    var byte = next()
    while (byte and FIRST_BIT_MASK == 0.toByte()) {
        result = (result shl 7) or byte.toInt()
        byte = next()
    }

    return (result shl 6) or (byte xor FIRST_BIT_MASK).toInt()
}

fun ByteArrayIterator.nextRawLong(): Long {
    var result = 0L
    var byte = next()
    while (byte and FIRST_BIT_MASK == 0.toByte()) {
        result = (result shl 7) or byte.toLong()
        byte = next()
    }

    return (result shl 6) or (byte xor FIRST_BIT_MASK).toLong()
}

private const val FIRST_BIT_MASK: Byte = 0b1000_0000.toByte()
