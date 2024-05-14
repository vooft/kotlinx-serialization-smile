package io.github.vooft.kotlinsmile.encoder.values

import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ValueSimpleLiteralWriterSessionTest {

    private val builder = ByteArrayBuilder()
    private val session = ValueSimpleLiteralWriterSession(builder)

    @Test
    fun should_write_empty_string() {
        session.valueEmptyString()
        builder.toByteArray() shouldBe byteArrayOf(0x20)
    }

    @Test
    fun should_write_null_value() {
        session.valueNull()
        builder.toByteArray() shouldBe byteArrayOf(0x21)
    }

    @Test
    fun should_write_false() {
        session.valueBoolean(false)
        builder.toByteArray() shouldBe byteArrayOf(0x22)
    }

    @Test
    fun should_write_true() {
        session.valueBoolean(true)
        builder.toByteArray() shouldBe byteArrayOf(0x23)
    }
}
