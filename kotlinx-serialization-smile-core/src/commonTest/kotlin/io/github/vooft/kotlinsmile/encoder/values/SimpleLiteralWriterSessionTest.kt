package io.github.vooft.kotlinsmile.encoder.values

import io.kotest.matchers.shouldBe
import kotlinx.io.bytestring.ByteStringBuilder
import kotlin.test.Test

class SimpleLiteralWriterSessionTest {
    @Test
    fun `should write empty string`() {
        val builder = ByteStringBuilder()
        val session = SimpleLiteralWriterSession(builder)

        session.emptyString()

        builder.toByteString().toByteArray() shouldBe byteArrayOf(0b001_00000u.toByte())
    }

    @Test
    fun `should write null`() {
        val builder = ByteStringBuilder()
        val session = SimpleLiteralWriterSession(builder)

        session.nullValue()

        builder.toByteString().toByteArray() shouldBe byteArrayOf(0b001_00001u.toByte())
    }

    @Test
    fun `should write true`() {
        val builder = ByteStringBuilder()
        val session = SimpleLiteralWriterSession(builder)

        session.boolean(true)

        builder.toByteString().toByteArray() shouldBe byteArrayOf(0b001_00010u.toByte())
    }

    @Test
    fun `should write false`() {
        val builder = ByteStringBuilder()
        val session = SimpleLiteralWriterSession(builder)

        session.boolean(false)

        builder.toByteString().toByteArray() shouldBe byteArrayOf(0b001_00011u.toByte())
    }
}
