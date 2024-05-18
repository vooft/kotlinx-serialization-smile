package io.github.vooft.kotlinsmile.encoder.values.raw

import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class RawNumberWriterTest {

    private val builder = ByteArrayBuilder()

    @Test
    fun should_append_raw_int() {
        val value = 12345

        builder.appendRawInt(value, AppendConfig.IntConfig)

        println(builder.toByteArray().toHexString())
        builder.toByteArray() shouldBe byteArrayOf(0x01, 0x40, 0xB9.toByte())
    }

    @Test
    fun should_append_raw_max_int() {
        val value = Int.MAX_VALUE

        builder.appendRawInt(value, AppendConfig.IntConfig)

        println(builder.toByteArray().toHexString())
        builder.toByteArray() shouldBe byteArrayOf(0x0F, 0x7F, 0x7F, 0x7F, 0xBF.toByte())
    }

    @Test
    fun should_append_raw_min_int() {
        val value = Int.MIN_VALUE

        builder.appendRawInt(value, AppendConfig.IntConfig)

        println(builder.toByteArray().toHexString())
        builder.toByteArray() shouldBe byteArrayOf(0x10, 0x00, 0x00, 0x00, 0x80.toByte())
    }

    @Test
    fun should_append_raw_long() {
        val value = 123456789L

        builder.appendRawLong(value, AppendConfig.LongConfig)

        println(builder.toByteArray().toHexString())
        builder.toByteArray() shouldBe byteArrayOf(0x75, 0x5E, 0x34, 0x95.toByte())
    }

    @Test
    fun should_append_raw_max_long() {
        val value = Long.MAX_VALUE

        builder.appendRawLong(value, AppendConfig.LongConfig)

        println(builder.toByteArray().toHexString())
        builder.toByteArray() shouldBe byteArrayOf(0x01, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F, 0xBF.toByte())
    }

    @Test
    fun should_append_raw_min_long() {
        val value = Long.MIN_VALUE

        builder.appendRawLong(value, AppendConfig.LongConfig)

        println(builder.toByteArray().toHexString())
        builder.toByteArray() shouldBe byteArrayOf(0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x80.toByte())
    }

    @Test
    fun should_append_raw_float() {
        val value = 123.456f

        builder.appendRawInt(value.toBits(), AppendConfig.FloatConfig)

        println(builder.toByteArray().toHexString())
        builder.toByteArray() shouldBe byteArrayOf(0x04, 0x17, 0x5B, 0x52, 0x79)
    }

    @Test
    fun should_append_raw_max_float() {
        val value = Float.MAX_VALUE

        builder.appendRawInt(value.toBits(), AppendConfig.FloatConfig)

        println(builder.toByteArray().toHexString())
        builder.toByteArray() shouldBe byteArrayOf(0x07, 0x7B, 0x7F, 0x7F, 0x7F)
    }

    @Test
    fun should_append_raw_min_float() {
        val value = Float.MIN_VALUE

        builder.appendRawInt(value.toBits(), AppendConfig.FloatConfig)

        println(builder.toByteArray().toHexString())
        builder.toByteArray() shouldBe byteArrayOf(0x00, 0x00, 0x00, 0x00, 0x01)
    }

    @Test
    fun should_append_raw_double() {
        val value = 12345.6789

        builder.appendRawLong(value.toBits(), AppendConfig.DoubleConfig)

        println(builder.toByteArray().toHexString())
        builder.toByteArray() shouldBe byteArrayOf(0x00, 0x40, 0x64, 0x07, 0x1A, 0x6E, 0x31, 0x47, 0x71, 0x21)
    }

    @Test
    fun should_append_raw_max_double() {
        val value = Double.MAX_VALUE

        builder.appendRawLong(value.toBits(), AppendConfig.DoubleConfig)

        println(builder.toByteArray().toHexString())
        builder.toByteArray() shouldBe byteArrayOf(0x00, 0x7F, 0x77, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F)
    }

    @Test
    fun should_append_raw_min_double() {
        val value = Double.MIN_VALUE

        builder.appendRawLong(value.toBits(), AppendConfig.DoubleConfig)

        println(builder.toByteArray().toHexString())
        builder.toByteArray() shouldBe byteArrayOf(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01)
    }
}

private fun ByteArray.toHexString() = joinToString(", ", "[", "]") { "0x" + it.toUByte().toString(16).padStart(2, '0').uppercase() }
