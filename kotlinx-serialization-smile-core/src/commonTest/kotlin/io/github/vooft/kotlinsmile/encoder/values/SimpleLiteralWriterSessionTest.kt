package io.github.vooft.kotlinsmile.encoder.values

import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class SimpleLiteralWriterSessionTest {
    @Test
    fun `should write empty string`() {
        val builder = ByteArrayBuilder()
        val session = ValueSimpleLiteralWriterSession(builder)

        session.emptyString()

        builder.toByteArray() shouldBe byteArrayOf(0b001_00000u.toByte())
    }

    @Test
    fun `should write null`() {
        val builder = ByteArrayBuilder()
        val session = ValueSimpleLiteralWriterSession(builder)

        session.nullValue()

        builder.toByteArray() shouldBe byteArrayOf(0b001_00001u.toByte())
    }

    @Test
    fun `should write true`() {
        val builder = ByteArrayBuilder()
        val session = ValueSimpleLiteralWriterSession(builder)

        session.boolean(true)

        builder.toByteArray() shouldBe byteArrayOf(0b001_00010u.toByte())
    }

    @Test
    fun `should write false`() {
        val builder = ByteArrayBuilder()
        val session = ValueSimpleLiteralWriterSession(builder)

        session.boolean(false)

        builder.toByteArray() shouldBe byteArrayOf(0b001_00011u.toByte())
    }
}
