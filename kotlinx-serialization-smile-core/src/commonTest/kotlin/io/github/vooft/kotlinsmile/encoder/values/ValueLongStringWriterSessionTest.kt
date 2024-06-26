package io.github.vooft.kotlinsmile.encoder.values

import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.github.vooft.kotlinsmile.common.toSmile
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ValueLongStringWriterSessionTest {

    private val builder = ByteArrayBuilder()
    private val session = ValueLongStringWriterSession(builder)

    @Test
    fun should_write_long_ascii() {
        val value = "a".repeat(1000)
        session.valueLongAscii(value.toSmile())

        builder.toByteArray() shouldBe byteArrayOf(
            0xE0.toByte(),
            *List(1000) { 0x61.toByte() }.toByteArray(),
            0xFC.toByte()
        )
    }

    @Test
    fun should_fail_to_write_long_ascii_for_unicode_string() {
        shouldThrow<IllegalArgumentException> {
            session.valueLongAscii(THREE_BYTE_CHAR.repeat(100).toSmile())
        }
    }

    @Test
    fun should_write_long_unicode() {
        val value = THREE_BYTE_CHAR.repeat(1000)
        session.valueLongUnicode(value.toSmile())

        builder.toByteArray() shouldBe byteArrayOf(
            0xE4.toByte(),
            *List(1000) { listOf(0xE2.toByte(), 0x82.toByte(), 0xAC.toByte()) }.flatten().toByteArray(),
            0xFC.toByte()
        )
    }

    @Test
    fun should_fail_to_write_long_unicode_for_ascii_string() {
        shouldThrow<IllegalArgumentException> {
            session.valueLongUnicode("a".repeat(1000).toSmile())
        }
    }
}

private const val THREE_BYTE_CHAR = "€"
