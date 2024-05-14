package io.github.vooft.kotlinsmile.encoder.values

import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class IntegerWriterSessionTest {

    private val builder = ByteArrayBuilder()
    private val session = IntegerWriterSession(builder)

    @Test
    fun should_encode_small_integer() {
        session.valueSmallInteger(10)
        builder.toByteArray() shouldBe byteArrayOf(
            0b11010100.toByte()
        )
    }

    @Test
    fun should_encode_regular_integer() {
        session.valueRegularInteger(100)
        builder.toByteArray() shouldBe byteArrayOf(
            0x24, // prefix
            0x03, 0x88.toByte()
        )
    }

    @Test
    fun should_encode_long_integer() {
        session.valueLongInteger(Int.MAX_VALUE.toLong() + 100)
        builder.toByteArray() shouldBe byteArrayOf(
            0x25, // prefix
            0x20, 0x00, 0x00, 0x03, 0x86.toByte()
        )
    }
}
