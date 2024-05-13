package io.github.vooft.kotlinsmile.encoder.values

import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.github.vooft.kotlinsmile.common.toSmile
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ValueShortStringWriterSessionTest {
    
    private val builder = ByteArrayBuilder()
    private val writer = ValueShortStringWriterSession(builder)

    @Test
    fun `should write tiny ascii`() {
        writer.valueTinyAscii("test123".toSmile())

        val expected = byteArrayOf(
            0x46.toByte(), // 0x40 offset + 6 length (starting from 1)
            0x74, 0x65, 0x73, 0x74, 0x31, 0x32, 0x33 // test123
        )

        val actual = builder.toByteArray()

        actual shouldBe expected
    }

    @Test
    fun `should fail tiny ascii for too long string`() {
        shouldThrow<IllegalArgumentException> {
            writer.valueShortAscii("1".repeat(65).toSmile())
        }
    }

    @Test
    fun `should fail tiny ascii for too short string`() {
        shouldThrow<IllegalArgumentException> {
            writer.valueShortAscii("".toSmile())
        }
    }

    @Test
    fun `should fail tiny ascii for non-ascii string`() {
        shouldThrow<IllegalArgumentException> {
            writer.valueTinyAscii(THREE_BYTE_CHAR.toSmile())
        }
    }

    @Test
    fun `should write short ascii`() {
        val value = "t".repeat(36)
        writer.valueShortAscii(value.toSmile())

        val expected = byteArrayOf(0x63.toByte()) + // 0x60 offset + 3 length (starting from 33)
            List(36) { 0x74.toByte() }.toByteArray() // t * 36

        val actual = builder.toByteArray()

        actual shouldBe expected
    }

    @Test
    fun `should fail short ascii for too long string`() {
        shouldThrow<IllegalArgumentException> {
            writer.valueShortAscii("1".repeat(65).toSmile())
        }
    }

    @Test
    fun `should fail short ascii for too short string`() {
        shouldThrow<IllegalArgumentException> {
            writer.valueShortAscii("1".repeat(32).toSmile())
        }
    }

    @Test
    fun `should fail short ascii for non-ascii string`() {
        shouldThrow<IllegalArgumentException> {
            writer.valueShortAscii(THREE_BYTE_CHAR.repeat(12).toSmile())
        }
    }

    @Test
    fun `should write tiny unicode`() {
        writer.valueTinyUnicode("hello $THREE_BYTE_CHAR".toSmile())

        val expected = byteArrayOf(
            0x87.toByte(),
            0x68, 0x65, 0x6C, 0x6C, 0x6F, 0x20, 0xE2.toByte(), 0x82.toByte(), 0xAC.toByte() // text
        )

        val actual = builder.toByteArray()

        actual shouldBe expected
    }

    @Test
    fun `should fail tiny unicode for too long string`() {
        shouldThrow<IllegalArgumentException> {
            writer.valueTinyUnicode(THREE_BYTE_CHAR.repeat(29).toSmile())
        }
    }

    @Test
    fun `should fail tiny unicode for non-unicode string`() {
        shouldThrow<IllegalArgumentException> {
            writer.valueTinyUnicode("abcd".toSmile())
        }
    }

    @Test
    fun `should write short unicode`() {
        writer.valueShortUnicode(THREE_BYTE_CHAR.repeat(15).toSmile())

        val expected = byteArrayOf(0xAB.toByte()) +
            List(15 ) { listOf(0xE2.toByte(), 0x82.toByte(), 0xAC.toByte()) }.flatten().toByteArray()

        val actual = builder.toByteArray()

        actual shouldBe expected
    }

    @Test
    fun `should fail short unicode for too long string`() {
        shouldThrow<IllegalArgumentException> {
            writer.valueShortUnicode(THREE_BYTE_CHAR.repeat(29).toSmile())
        }
    }

    @Test
    fun `should fail short unicode for too short string`() {
        shouldThrow<IllegalArgumentException> {
            writer.valueShortUnicode(THREE_BYTE_CHAR.toSmile())
        }
    }


    @Test
    fun `should fail short unicode for non-unicode string`() {
        shouldThrow<IllegalArgumentException> {
            writer.valueShortUnicode("abcd".toSmile())
        }
    }
}

private const val THREE_BYTE_CHAR = "€"
