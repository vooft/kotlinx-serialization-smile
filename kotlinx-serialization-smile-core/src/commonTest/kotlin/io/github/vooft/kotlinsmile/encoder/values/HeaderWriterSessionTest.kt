package io.github.vooft.kotlinsmile.encoder.values

import io.github.vooft.kotlinsmile.SmileConfig
import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.github.vooft.kotlinsmile.encoder.structure.HeaderWriterSession
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class HeaderWriterSessionTest {
    @Test
    fun should_preallocate_header() {
        val builder = ByteArrayBuilder()
        val session = HeaderWriterSession(builder)

        session.preallocateHeader()

        val actual = builder.toByteArray()
        actual shouldBe byteArrayOf(0x3A, 0x29, 0x0A, 0)
    }

    @Test
    fun should_overwrite_preallocated_header() {
        val builder = ByteArrayBuilder()
        val session = HeaderWriterSession(builder)

        session.preallocateHeader()

        val actual = session.toByteArray(
            config = SmileConfig(writeHeader = true, shareStringValue = true, sharePropertyName = true),
        )
//        actual shouldBe byteArrayOf(0x3A, 0x29, 0x0A, 0x7)
        actual shouldBe byteArrayOf(0x3A, 0x29, 0x0A, 0x3) // for some reason jackson doesn't set the raw binary bit
    }

    @Test
    fun should_fail_to_write_header_before_preallocation() {
        val builder = ByteArrayBuilder()
        val session = HeaderWriterSession(builder)

        shouldThrow<IllegalArgumentException> {
            session.toByteArray(
                config = SmileConfig(writeHeader = true, shareStringValue = true, sharePropertyName = true),
            )
        }
    }
}
