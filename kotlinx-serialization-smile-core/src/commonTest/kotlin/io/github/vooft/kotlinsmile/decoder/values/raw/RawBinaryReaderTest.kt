package io.github.vooft.kotlinsmile.decoder.values.raw

import io.github.vooft.kotlinsmile.common.ByteArrayIteratorImpl
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class RawBinaryReaderTest {
    @Test
    fun `should read one bit`() {
        val encoded =  ubyteArrayOf(0b0111_1111u, 0x1u).toByteArray()
        val iterator = ByteArrayIteratorImpl(encoded)
        val expected = byteArrayOf(0xFF.toByte())

        val actual = iterator.nextRawBinary(encoded.size)

        println("E: ${expected.toBinaryString()}")
        println("A: ${actual.toBinaryString()}")

        actual shouldBe expected
    }

    @Test
    fun `should read 8 bits`() {
        val encoded = byteArrayOf(0b0111_1111, 0b0000_0001)
        val iterator = ByteArrayIteratorImpl(encoded)
        val expected = byteArrayOf(0b1111_1111u.toByte())

        val actual = iterator.nextRawBinary(encoded.size)

        println("E: ${expected.toBinaryString()}")
        println("A: ${actual.toBinaryString()}")

        actual shouldBe expected
    }

    @Test
    fun `should read 16 bits`() {
        val encoded = byteArrayOf(0b0111_1111, 0b0111_1111, 0b0000_0011)
        val iterator = ByteArrayIteratorImpl(encoded)
        val expected = byteArrayOf(0b1111_1111u.toByte(), 0b1111_1111u.toByte())

        val actual = iterator.nextRawBinary(encoded.size)

        println("E: ${expected.toBinaryString()}")
        println("A: ${actual.toBinaryString()}")

        actual shouldBe expected
    }

    @Test
    fun `should deserialize 64 bits`() {
        val encoded = ByteArray(10) { 0x7F.toByte() }.also { it[9] = 0x01 }
        val iterator = ByteArrayIteratorImpl(encoded)
        val expected = ByteArray(8) { 0xFF.toByte() }

        val actual = iterator.nextRawBinary(encoded.size)

        println("E: ${expected.toBinaryString()}")
        println("A: ${actual.toBinaryString()}")

        actual shouldBe expected
    }

    @Test
    fun `should deserialize random bytes`() {
        val encoded = byteArrayOf(0x4e, 0x31, 0x21, 0x1e, 0x20, 0x6a, 0x08, 0x08, 0x7d, 0x6f, 0x6d, 0x07)
        val iterator = ByteArrayIteratorImpl(encoded)
        val expected = byteArrayOf(0x9c.toByte(), 0xc5.toByte(), 0x09, 0xe4.toByte(), 0x1a, 0x84.toByte(), 0x08, 0xfb.toByte(), 0xbf.toByte(), 0x6f)

        val actual = iterator.nextRawBinary(encoded.size)

        println("E: ${expected.toBinaryString()}")
        println("A: ${actual.toBinaryString()}")

        actual shouldBe expected
    }

    @Test
    fun `should deserialize 3 bytes`() {
        val encoded = byteArrayOf(0x01, 0x40, 0x40, 0x01)
        val iterator = ByteArrayIteratorImpl(encoded)

        val expected = byteArrayOf(3, 2, 1)
        val actual = iterator.nextRawBinary(encoded.size)

        println("E: ${expected.toBinaryString()}")
        println("A: ${actual.toBinaryString()}")

        actual shouldBe expected
    }
}

private fun ByteArray.toHexString() = joinToString(", ", "[", "]") { it.toUByte().toString(16).padStart(2, '0') } + "]"
private fun ByteArray.toBinaryString() = joinToString(", ", "[", "]") { it.toUByte().toString(2).padStart(8, '0') }
