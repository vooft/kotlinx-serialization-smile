package io.github.vooft.kotlinsmile.encoder.keys

import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.github.vooft.kotlinsmile.common.toSmile
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class KeyStringWriterSessionTest {

    private val builder = ByteArrayBuilder()
    private val writer = KeyStringWriterSession(builder)

    @Test
    fun `should write key short ascii`() {
        writer.keyShortAscii("test123".toSmile())

        val expected = byteArrayOf(
            0x86.toByte(), // 0x80 offset + 6 length (starting from 1)
            0x74, 0x65, 0x73, 0x74, 0x31, 0x32, 0x33 // test123
        )

        val actual = builder.toByteArray()

        actual shouldBe expected
    }

    @Test
    fun `should fail key short ascii for too long string`() {
        shouldThrow<IllegalArgumentException> {
            writer.keyShortAscii("1".repeat(65).toSmile())
        }
    }

    @Test
    fun `should fail key short ascii for too short string`() {
        shouldThrow<IllegalArgumentException> {
            writer.keyShortAscii("".toSmile())
        }
    }

    @Test
    fun `should fail key short ascii for non-ascii string`() {
        shouldThrow<IllegalArgumentException> {
            writer.keyShortAscii(THREE_BYTE_CHAR.toSmile())
        }
    }

    @Test
    fun `should write key short unicode`() {
        writer.keyShortUnicode("hello $THREE_BYTE_CHAR".toSmile())

        val expected = byteArrayOf(
            0xC7.toByte(), // 0x90 offset + 6 length (starting from 1)
            0x68, 0x65, 0x6C, 0x6C, 0x6F, 0x20, 0xE2.toByte(), 0x82.toByte(), 0xAC.toByte() // text
        )

        val actual = builder.toByteArray()

        actual shouldBe expected
    }

    @Test
    fun `should fail key short unicode for too long string`() {
        shouldThrow<IllegalArgumentException> {
            writer.keyShortUnicode(THREE_BYTE_CHAR.repeat(29).toSmile())
        }
    }

    @Test
    fun `should fail key short unicode for non-unicode string`() {
        shouldThrow<IllegalArgumentException> {
            writer.keyShortUnicode("abcd".toSmile())
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `should encode long unicode`() {
        val repeats = 25
        writer.keyLongUnicode(THREE_BYTE_CHAR.repeat(repeats).toSmile())

        val encodedUnicode = THREE_BYTE_CHAR.encodeToByteArray().toList()
        val expected = byteArrayOf(0x34) + // long unicode prefix
                List(repeats) { encodedUnicode }.flatten().toByteArray() +
                byteArrayOf(0xFC.toByte())

        val actual = builder.toByteArray()
        println(actual.toHexString())
        actual shouldBe expected
    }

    @Test
    fun `should fail key long unicode for too short string`() {
        shouldThrow<IllegalArgumentException> {
            writer.keyLongUnicode(THREE_BYTE_CHAR.repeat(21).toSmile())
        }
    }
}

private const val THREE_BYTE_CHAR = "€"
