package io.github.vooft.kotlinsmile.decoder.values

import io.github.vooft.kotlinsmile.common.ByteArrayIteratorImpl
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class IntegerReaderSessionTest {
    @Test
    fun should_read_small_integer() {
        val iterator = ByteArrayIteratorImpl(byteArrayOf(
            0xC1.toByte() // 0xC0 offset + "-1" in zigzag
        ))
        val reader = IntegerReaderSession(iterator)

        val result = reader.valueSmallInteger()

        result shouldBe -1
    }

    @Test
    fun should_fail_small_integer_on_invalid_range() {
        val iterator = ByteArrayIteratorImpl(byteArrayOf(
            0x00.toByte()
        ))

        val reader = IntegerReaderSession(iterator)

        shouldThrow<IllegalArgumentException> { reader.valueSmallInteger() }
    }

    @Test
    fun should_read_regular_integer() {
        val iterator = ByteArrayIteratorImpl(byteArrayOf(
            0x24.toByte(), // regular integer token
            0b0011_1111, 0b1000_0000.toByte(),
        ))
        val reader = IntegerReaderSession(iterator)

        val result = reader.valueRegularInteger()

        result shouldBe 2016
    }

    @Test
    fun should_fail_regular_integer_on_invalid_token() {
        val iterator = ByteArrayIteratorImpl(byteArrayOf(
            0x00.toByte()
        ))

        val reader = IntegerReaderSession(iterator)

        shouldThrow<IllegalArgumentException> { reader.valueRegularInteger() }
    }

    @Test
    fun should_read_long_integer() {
        val iterator = ByteArrayIteratorImpl(byteArrayOf(
            0x25.toByte(), // long integer token
            *ByteArray(10) { 0x7F.toByte() }.also { it[9] = 0b1011_1111.toByte() }
        ))
        val reader = IntegerReaderSession(iterator)

        val result = reader.valueLongInteger()

        result shouldBe Long.MIN_VALUE
    }

    @Test
    fun should_fail_long_integer_on_invalid_token() {
        val iterator = ByteArrayIteratorImpl(byteArrayOf(
            0x00.toByte()
        ))

        val reader = IntegerReaderSession(iterator)

        shouldThrow<IllegalArgumentException> { reader.valueLongInteger() }
    }
}
