package io.github.vooft.kotlinsmile.encoder.values

import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.github.vooft.kotlinsmile.common.ZigzagInteger
import io.github.vooft.kotlinsmile.token.SmileValueToken.LongInteger
import io.github.vooft.kotlinsmile.token.SmileValueToken.RegularInteger
import io.github.vooft.kotlinsmile.token.SmileValueToken.SmallInteger
import kotlin.experimental.or
import kotlin.jvm.JvmInline

interface IntegerWriter {
    fun smallInteger(value: Int)
    fun regularInteger(value: Int)
    fun longInteger(value: Long)
}

@JvmInline
value class IntegerWriterSession(private val builder: ByteArrayBuilder) : IntegerWriter {
    override fun smallInteger(value: Int) {
        require(value in SmallInteger.values) { "Value must be in ${SmallInteger.values}, actual: $value" }

        val zigzag = ZigzagInteger.encode(value)
        builder.append(byte = zigzag.toByte(), offset = SmallInteger.offset)
    }

    override fun regularInteger(value: Int) {
        require(value in RegularInteger.values) { "Value must be in ${RegularInteger.values}, actual: $value" }

        builder.append(byte = RegularInteger.firstByte)

        val bytes = ByteArray(BIT_SHIFTS_32.size)

        var bytesTotal = 0
        var remainingZigzag = ZigzagInteger.encode(value).toUInt()
        for (shift in BIT_SHIFTS_32) {
            val bitMask = ALL_ONES_BYTE shr (8 - shift)
            bytes[bytesTotal++] = (remainingZigzag and bitMask).toByte()
            if (remainingZigzag <= bitMask) {
                break
            }

            remainingZigzag = remainingZigzag shr shift
        }

        for (i in bytesTotal - 1 downTo 1) {
            builder.append(byte = bytes[i])
        }

        builder.append(byte = bytes[0] or FIRST_BIT_MASK.toByte())
    }

    override fun longInteger(value: Long) {
        require(value in LongInteger.values) { "Value must be in ${LongInteger.values}, actual: $value" }

        builder.append(byte = LongInteger.firstByte)

        val bytes = ByteArray(BIT_SHIFTS_64.size)

        var bytesTotal = 0
        var remainingZigzag = ZigzagInteger.encode(value).toULong()
        for (shift in BIT_SHIFTS_64) {
            val bitMask = ALL_ONES_BYTE shr (8 - shift)
            bytes[bytesTotal++] = (remainingZigzag and bitMask.toULong()).toByte()
            if (remainingZigzag <= bitMask) {
                break
            }

            remainingZigzag = remainingZigzag shr shift
        }

        for (i in bytesTotal - 1 downTo 1) {
            builder.append(byte = bytes[i])
        }

        builder.append(byte = bytes[0] or FIRST_BIT_MASK.toByte())
    }
}

private const val ALL_ONES_BYTE = 0b1111_1111u
private const val FIRST_BIT_MASK = 0b1000_0000u
private val BIT_SHIFTS_32 = listOf(6, 7, 7, 7, 7)
private val BIT_SHIFTS_64 = listOf(6, 7, 7, 7, 7, 7, 7, 7, 7, 7)

