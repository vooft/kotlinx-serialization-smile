package io.github.vooft.kotlinsmile.decoder.raw

import io.github.vooft.kotlinsmile.common.ByteArrayIteratorImpl
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class RawNumberReaderTest {
    @Test
    fun should_read_raw_int() {
        val expected = 12345
        val iterator = ByteArrayIteratorImpl(byteArrayOf(0x01, 0x40, 0xB9.toByte()))

        iterator.nextRawInt() shouldBe expected
    }

    @Test
    fun should_read_raw_max_int() {
        val expected = Int.MAX_VALUE
        val iterator = ByteArrayIteratorImpl(byteArrayOf(0x0F, 0x7F, 0x7F, 0x7F, 0xBF.toByte()))

        iterator.nextRawInt() shouldBe expected
    }

    @Test
    fun should_read_raw_min_int() {
        val expected = Int.MIN_VALUE
        val iterator = ByteArrayIteratorImpl(byteArrayOf(0x10, 0x00, 0x00, 0x00, 0x80.toByte()))
        
        iterator.nextRawInt() shouldBe expected
    }

    @Test
    fun should_read_raw_long() {
        val expected = 123456789L
        val iterator = ByteArrayIteratorImpl(byteArrayOf(0x75, 0x5E, 0x34, 0x95.toByte()))

        iterator.nextRawLong() shouldBe expected
    }

    @Test
    fun should_read_raw_max_long() {
        val expected = Long.MAX_VALUE
        val iterator = ByteArrayIteratorImpl(byteArrayOf(0x01, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F, 0xBF.toByte()))

        iterator.nextRawLong() shouldBe expected
    }

    @Test
    fun should_read_raw_min_long() {
        val expected = Long.MIN_VALUE
        val iterator = ByteArrayIteratorImpl(byteArrayOf(0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x80.toByte()))

        iterator.nextRawLong() shouldBe expected
    }
}
