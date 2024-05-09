package io.github.vooft.kotlinsmile.encoder.values

import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class HeaderWriterSessionTest {
    @Test
    fun `should write header`() {
        val builder = ByteArrayBuilder()
        val session = HeaderWriterSession(builder)

        session.header()

        val actual = builder.toByteArray()
        actual shouldBe byteArrayOf(0x3A, 0x29, 0x0A, 0)
    }
}
