package io.github.vooft.kotlinsmile.encoder.values

import io.kotest.matchers.shouldBe
import kotlinx.io.bytestring.ByteStringBuilder
import kotlin.test.Test

class HeaderWriterSessionTest {
    @Test
    fun `should write header`() {
        val builder = ByteStringBuilder()
        val session = HeaderWriterSession(builder)

        session.header()

        val actual = builder.toByteString().toByteArray()
        actual shouldBe byteArrayOf(0x3A, 0x29, 0x0A, 0)
    }
}
