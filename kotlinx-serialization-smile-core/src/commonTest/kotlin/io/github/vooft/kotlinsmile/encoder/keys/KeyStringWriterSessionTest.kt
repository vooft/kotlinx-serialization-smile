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
    fun should_write_key_short_ascii() {
        writer.keyShortAscii("test123".toSmile())

        val expected = byteArrayOf(
            0x86.toByte(), // 0x80 offset + 6 length (starting from 1)
            0x74, 0x65, 0x73, 0x74, 0x31, 0x32, 0x33 // test123
        )

        val actual = builder.toByteArray()

        actual shouldBe expected
    }

    @Test
    fun should_fail_key_short_ascii_for_too_long_string() {
        shouldThrow<IllegalArgumentException> {
            writer.keyShortAscii("1".repeat(65).toSmile())
        }
    }

    @Test
    fun should_fail_key_short_ascii_for_too_short_string() {
        shouldThrow<IllegalArgumentException> {
            writer.keyShortAscii("".toSmile())
        }
    }

    @Test
    fun should_fail_key_short_ascii_for_non_ascii_string() {
        shouldThrow<IllegalArgumentException> {
            writer.keyShortAscii(THREE_BYTE_CHAR.toSmile())
        }
    }

    @Test
    fun should_write_key_short_unicode() {
        writer.keyShortUnicode("hello $THREE_BYTE_CHAR".toSmile())

        val expected = byteArrayOf(
            0xC7.toByte(), // 0x90 offset + 6 length (starting from 1)
            0x68, 0x65, 0x6C, 0x6C, 0x6F, 0x20, 0xE2.toByte(), 0x82.toByte(), 0xAC.toByte() // text
        )

        val actual = builder.toByteArray()

        actual shouldBe expected
    }

    @Test
    fun should_fail_key_short_unicode_for_too_long_string() {
        shouldThrow<IllegalArgumentException> {
            writer.keyShortUnicode(THREE_BYTE_CHAR.repeat(29).toSmile())
        }
    }

    @Test
    fun should_fail_key_short_unicode_for_non_unicode_string() {
        shouldThrow<IllegalArgumentException> {
            writer.keyShortUnicode("abcd".toSmile())
        }
    }

    @Test
    fun should_encode_long_unicode() {
        val repeats = 25
        writer.keyLongUnicode(THREE_BYTE_CHAR.repeat(repeats).toSmile())

        val encodedUnicode = THREE_BYTE_CHAR.encodeToByteArray().toList()
        val expected = byteArrayOf(
            0x34, // long unicode prefix
            *List(repeats) { encodedUnicode }.flatten().toByteArray(),
            0xFC.toByte()
        )

        val actual = builder.toByteArray()
        actual shouldBe expected
    }

    @Test
    fun should_fail_key_long_unicode_for_too_short_string() {
        shouldThrow<IllegalArgumentException> {
            writer.keyLongUnicode(THREE_BYTE_CHAR.repeat(21).toSmile())
        }
    }
}

private const val THREE_BYTE_CHAR = "€"
