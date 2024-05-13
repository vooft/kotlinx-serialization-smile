package io.github.vooft.kotlinsmile.encoder.values.raw

import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class RawBinaryWriterTest {
    @Test
    fun `should write one bit`() {
        val builder = ByteArrayBuilder()

        builder.appendRawBinary(byteArrayOf(0xFF.toByte()))

        builder.toByteArray() shouldBe byteArrayOf(0b111_1111, 0x1)
    }

    @Test
    fun `should write 8 bits`() {
        val builder = ByteArrayBuilder()

        builder.appendRawBinary(byteArrayOf(0b1111_1111u.toByte()))

        val expected = byteArrayOf(0b0111_1111, 0b0000_0001)
        val actual = builder.toByteArray()

        println("E: ${expected.toBinaryString()}")
        println("A: ${actual.toBinaryString()}")

        actual shouldBe expected
    }

    @Test
    fun `should write 16 bits`() {
        val builder = ByteArrayBuilder()

        builder.appendRawBinary(byteArrayOf(0b1111_1111u.toByte(), 0b1111_1111u.toByte()))

        val expected = byteArrayOf(0b0111_1111, 0b0111_1111, 0b0000_0011)
        val actual = builder.toByteArray()

        println("E: ${expected.toBinaryString()}")
        println("A: ${actual.toBinaryString()}")

        actual shouldBe expected
    }

    @Test
    fun `should serialize 64 bits`() {
        val builder = ByteArrayBuilder()

        builder.appendRawBinary(ByteArray(8) { 0xFF.toByte() })

        val expected = ByteArray(10) { 0x7F.toByte() }
        expected[9] = 0x01
        val actual = builder.toByteArray()

        println("E: ${expected.toBinaryString()}")
        println("A: ${actual.toBinaryString()}")

        actual shouldBe expected
    }
    
    @Test
    fun `should serialize random bytes`() {
        val data = byteArrayOf(
            0x9c.toByte(), 0xc5.toByte(), 0x09, 0xe4.toByte(), 0x1a, 0x84.toByte(), 0x08, 0xfb.toByte(), 0xbf.toByte(), 0x6f
        )

        val builder = ByteArrayBuilder()
        builder.appendRawBinary(data)

        val expected = byteArrayOf(0x4e, 0x31, 0x21, 0x1e, 0x20, 0x6a, 0x08, 0x08, 0x7d, 0x6f, 0x6d, 0x07)
        val actual = builder.toByteArray()

        println("E: ${expected.toBinaryString()}")
        println("A: ${actual.toBinaryString()}")

        actual shouldBe expected
    }

    @Test
    fun test() {
        val builder = ByteArrayBuilder()
        builder.appendRawBinary(byteArrayOf(3, 2, 1))
        println(builder.toByteArray().toHexString())
    }
}

private fun ByteArray.toHexString() = joinToString(", ", "[", "]") { it.toUByte().toString(16).padStart(2, '0') } + "]"
private fun ByteArray.toBinaryString() = joinToString(", ", "[", "]") { it.toUByte().toString(2).padStart(8, '0') }
