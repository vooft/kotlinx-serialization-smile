package io.github.vooft.kotlinsmile.encoder.values

import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.github.vooft.kotlinsmile.token.SmileValueToken.DoubleValue
import io.github.vooft.kotlinsmile.token.SmileValueToken.FloatValue
import kotlin.experimental.or
import kotlin.math.max

fun ByteArrayBuilder.appendRawInt(value: Int, config: AppendConfig) {
    val bytes = ByteArray(config.bitShifts.size)

    var remaining = value.toUInt()
    var bytesTotal = 0
    for (shift in config.bitShifts) {
        val bitMask = ALL_ONES_BYTE shr (8 - shift)
        bytes[bytesTotal++] = (remaining and bitMask).toByte()
        if (remaining <= bitMask) {
            break
        }

        remaining = remaining shr shift
    }

    for (i in max(bytesTotal - 1, config.minBytes - 1) downTo 1) {
        append(byte = bytes[i])
    }

    append(byte = bytes[0] or config.lastByteOrMask)
}

fun ByteArrayBuilder.appendRawLong(value: Long, config: AppendConfig) {
    val bytes = ByteArray(config.bitShifts.size)

    var bytesTotal = 0
    var remaining = value.toULong()
    for (shift in config.bitShifts) {
        val bitMask = ALL_ONES_BYTE shr (8 - shift)
        bytes[bytesTotal++] = (remaining and bitMask.toULong()).toByte()
        if (remaining <= bitMask) {
            break
        }

        remaining = remaining shr shift
    }

    for (i in max(bytesTotal - 1, config.minBytes - 1) downTo 1) {
        append(byte = bytes[i])
    }

    append(byte = bytes[0] or config.lastByteOrMask)
}

sealed interface AppendConfig {
    val minBytes: Int
    val bitShifts: List<Int>
    val lastByteOrMask: Byte

    object IntConfig : AppendConfig {
        override val minBytes = 1
        override val bitShifts = listOf(6, 7, 7, 7, 7)
        override val lastByteOrMask = FIRST_BIT_MASK.toByte()
    }

    object LongConfig : AppendConfig {
        override val minBytes = 1
        override val bitShifts = listOf(6, 7, 7, 7, 7, 7, 7, 7, 7, 7)
        override val lastByteOrMask = FIRST_BIT_MASK.toByte()
    }

    object FloatConfig : AppendConfig {
        override val minBytes = FloatValue.SERIALIZED_BYTES
        override val bitShifts = List(minBytes) { 7 }
        override val lastByteOrMask = 0.toByte()
    }

    object DoubleConfig : AppendConfig {
        override val minBytes = DoubleValue.SERIALIZED_BYTES
        override val bitShifts = List(minBytes) { 7 }
        override val lastByteOrMask = 0.toByte()
    }
}

private const val ALL_ONES_BYTE = 0b1111_1111u
private const val FIRST_BIT_MASK = 0b1000_0000u
