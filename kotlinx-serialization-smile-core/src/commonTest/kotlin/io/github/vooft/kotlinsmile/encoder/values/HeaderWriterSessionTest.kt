package io.github.vooft.kotlinsmile.encoder.values

import io.github.vooft.kotlinsmile.SmileConfig
import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.github.vooft.kotlinsmile.encoder.structure.HeaderWriterSession
import io.kotest.matchers.shouldBe

class HeaderWriterSessionTest {
//    @Test
    fun should_write_header() {
        val builder = ByteArrayBuilder()
        val session = HeaderWriterSession(builder)

        session.header(SmileConfig.DEFAULT)

        val actual = builder.toByteArray()
        actual shouldBe byteArrayOf(0x3A, 0x29, 0x0A, 0)
    }
}
