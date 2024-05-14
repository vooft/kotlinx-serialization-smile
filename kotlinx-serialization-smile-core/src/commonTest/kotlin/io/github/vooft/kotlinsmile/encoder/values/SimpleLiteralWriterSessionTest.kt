package io.github.vooft.kotlinsmile.encoder.values

import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class SimpleLiteralWriterSessionTest {
    @Test
    fun should_write_empty_string() {
        val builder = ByteArrayBuilder()
        val session = ValueSimpleLiteralWriterSession(builder)

        session.valueEmptyString()

        builder.toByteArray() shouldBe byteArrayOf(0b001_00000u.toByte())
    }

    @Test
    fun should_write_null() {
        val builder = ByteArrayBuilder()
        val session = ValueSimpleLiteralWriterSession(builder)

        session.valueNull()

        builder.toByteArray() shouldBe byteArrayOf(0b001_00001u.toByte())
    }

    @Test
    fun should_write_true() {
        val builder = ByteArrayBuilder()
        val session = ValueSimpleLiteralWriterSession(builder)

        session.valueBoolean(true)

        builder.toByteArray() shouldBe byteArrayOf(0b001_00011u.toByte())
    }

    @Test
    fun should_write_false() {
        val builder = ByteArrayBuilder()
        val session = ValueSimpleLiteralWriterSession(builder)

        session.valueBoolean(false)

        builder.toByteArray() shouldBe byteArrayOf(0b001_00010u.toByte())
    }
}
