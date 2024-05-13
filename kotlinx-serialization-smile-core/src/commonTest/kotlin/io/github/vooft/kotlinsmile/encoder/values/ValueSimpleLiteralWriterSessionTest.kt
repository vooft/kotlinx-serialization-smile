package io.github.vooft.kotlinsmile.encoder.values

import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ValueSimpleLiteralWriterSessionTest {

    private val builder = ByteArrayBuilder()
    private val session = ValueSimpleLiteralWriterSession(builder)

    @Test
    fun `should write empty string`() {
        session.valueEmptyString()
        builder.toByteArray() shouldBe byteArrayOf(0x20)
    }

    @Test
    fun `should write null value`() {
        session.valueNull()
        builder.toByteArray() shouldBe byteArrayOf(0x21)
    }

    @Test
    fun `should write false`() {
        session.valueBoolean(false)
        builder.toByteArray() shouldBe byteArrayOf(0x22)
    }

    @Test
    fun `should write true`() {
        session.valueBoolean(true)
        builder.toByteArray() shouldBe byteArrayOf(0x23)
    }
}
