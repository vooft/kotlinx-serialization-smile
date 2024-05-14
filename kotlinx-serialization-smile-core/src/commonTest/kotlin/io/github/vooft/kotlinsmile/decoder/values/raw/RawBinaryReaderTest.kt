package io.github.vooft.kotlinsmile.decoder.values.raw

import io.github.vooft.kotlinsmile.common.ByteArrayIteratorImpl
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class RawBinaryReaderTest {
    @Test
    fun should_read_one_bit() {
        val encoded =  byteArrayOf(0b111_1111, 0x1)
        val iterator = ByteArrayIteratorImpl(encoded)
        val expected = byteArrayOf(0xFF.toByte())

        val actual = iterator.nextRawBinary(expected.size)

        println("E: ${expected.toBinaryString()}")
        println("A: ${actual.toBinaryString()}")

        actual shouldBe expected
    }

    @Test
    fun should_read_8_bits() {
        val encoded = byteArrayOf(0b0111_1111, 0b0000_0001)
        val iterator = ByteArrayIteratorImpl(encoded)
        val expected = byteArrayOf(0b1111_1111u.toByte())

        val actual = iterator.nextRawBinary(expected.size)

        println("E: ${expected.toBinaryString()}")
        println("A: ${actual.toBinaryString()}")

        actual shouldBe expected
    }

    @Test
    fun should_read_16_bits() {
        val encoded = byteArrayOf(0b0111_1111, 0b0111_1111, 0b0000_0011)
        val iterator = ByteArrayIteratorImpl(encoded)
        val expected = byteArrayOf(0b1111_1111u.toByte(), 0b1111_1111u.toByte())

        val actual = iterator.nextRawBinary(expected.size)

        println("E: ${expected.toBinaryString()}")
        println("A: ${actual.toBinaryString()}")

        actual shouldBe expected
    }

    @Test
    fun should_deserialize_64_bits() {
        val encoded = ByteArray(10) { 0x7F.toByte() }.also { it[9] = 0x01 }
        val iterator = ByteArrayIteratorImpl(encoded)
        val expected = ByteArray(8) { 0xFF.toByte() }

        val actual = iterator.nextRawBinary(expected.size)

        println("E: ${expected.toBinaryString()}")
        println("A: ${actual.toBinaryString()}")

        actual shouldBe expected
    }

    @Test
    fun should_deserialize_random_bytes() {
        val encoded = byteArrayOf(0x4e, 0x31, 0x21, 0x1e, 0x20, 0x6a, 0x08, 0x08, 0x7d, 0x6f, 0x6d, 0x07)
        val iterator = ByteArrayIteratorImpl(encoded)
        val expected = byteArrayOf(
            0x9c.toByte(), 0xc5.toByte(), 0x09, 0xe4.toByte(), 0x1a, 0x84.toByte(), 0x08, 0xfb.toByte(), 0xbf.toByte(), 0x6f
        )

        val actual = iterator.nextRawBinary(expected.size)

        println("E: ${expected.toBinaryString()}")
        println("A: ${actual.toBinaryString()}")

        actual shouldBe expected
    }

    @Test
    fun should_deserialize_3_bytes() {
        val encoded = byteArrayOf(0x01, 0x40, 0x40, 0x01)
        val iterator = ByteArrayIteratorImpl(encoded)

        val expected = byteArrayOf(3, 2, 1)
        val actual = iterator.nextRawBinary(expected.size)

        println("E: ${expected.toBinaryString()}")
        println("A: ${actual.toBinaryString()}")

        actual shouldBe expected
    }
}

private fun ByteArray.toBinaryString() = joinToString(", ", "[", "]") { it.toUByte().toString(2).padStart(8, '0') }
