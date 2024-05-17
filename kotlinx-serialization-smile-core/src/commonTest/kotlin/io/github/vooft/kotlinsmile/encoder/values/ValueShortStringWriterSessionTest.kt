package io.github.vooft.kotlinsmile.encoder.values

import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.github.vooft.kotlinsmile.common.shared.SmileSharedStorageImpl
import io.github.vooft.kotlinsmile.common.toSmile
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test

class ValueShortStringWriterSessionTest {
    
    private val builder = ByteArrayBuilder()
    private val sharedStorage = SmileSharedStorageImpl(shareKeys = false, shareValues = true)
    private val writer = ValueShortStringWriterSession(builder, sharedStorage)

    @Test
    fun should_write_tiny_ascii() {
        val value = "test123"
        writer.valueTinyAscii(value.toSmile())

        val expected = byteArrayOf(
            0x46.toByte(), // 0x40 offset + 6 length (starting from 1)
            0x74, 0x65, 0x73, 0x74, 0x31, 0x32, 0x33 // test123
        )

        val actual = builder.toByteArray()

        actual shouldBe expected
        sharedStorage.lookupValue(value) shouldNotBe null
    }

    @Test
    fun should_fail_tiny_ascii_for_too_long_string() {
        shouldThrow<IllegalArgumentException> {
            writer.valueShortAscii("1".repeat(65).toSmile())
        }
    }

    @Test
    fun should_fail_tiny_ascii_for_too_short_string() {
        shouldThrow<IllegalArgumentException> {
            writer.valueShortAscii("".toSmile())
        }
    }

    @Test
    fun should_fail_tiny_ascii_for_non_ascii_string() {
        shouldThrow<IllegalArgumentException> {
            writer.valueTinyAscii(THREE_BYTE_CHAR.toSmile())
        }
    }

    @Test
    fun should_write_short_ascii() {
        val value = "t".repeat(36)
        writer.valueShortAscii(value.toSmile())

        val expected = byteArrayOf(0x63.toByte()) + // 0x60 offset + 3 length (starting from 33)
            List(36) { 0x74.toByte() }.toByteArray() // t * 36

        val actual = builder.toByteArray()

        actual shouldBe expected
        sharedStorage.lookupValue(value) shouldNotBe null
    }

    @Test
    fun should_fail_short_ascii_for_too_long_string() {
        shouldThrow<IllegalArgumentException> {
            writer.valueShortAscii("1".repeat(65).toSmile())
        }
    }

    @Test
    fun should_fail_short_ascii_for_too_short_string() {
        shouldThrow<IllegalArgumentException> {
            writer.valueShortAscii("1".repeat(32).toSmile())
        }
    }

    @Test
    fun should_fail_short_ascii_for_non_ascii_string() {
        shouldThrow<IllegalArgumentException> {
            writer.valueShortAscii(THREE_BYTE_CHAR.repeat(12).toSmile())
        }
    }

    @Test
    fun should_write_tiny_unicode() {
        val value = "hello $THREE_BYTE_CHAR"
        writer.valueTinyUnicode(value.toSmile())

        val expected = byteArrayOf(
            0x87.toByte(),
            0x68, 0x65, 0x6C, 0x6C, 0x6F, 0x20, 0xE2.toByte(), 0x82.toByte(), 0xAC.toByte() // text
        )

        val actual = builder.toByteArray()

        actual shouldBe expected
        sharedStorage.lookupValue(value) shouldNotBe null
    }

    @Test
    fun should_fail_tiny_unicode_for_too_long_string() {
        shouldThrow<IllegalArgumentException> {
            writer.valueTinyUnicode(THREE_BYTE_CHAR.repeat(29).toSmile())
        }
    }

    @Test
    fun should_fail_tiny_unicode_for_non_unicode_string() {
        shouldThrow<IllegalArgumentException> {
            writer.valueTinyUnicode("abcd".toSmile())
        }
    }

    @Test
    fun should_write_short_unicode() {
        val value = THREE_BYTE_CHAR.repeat(15)
        writer.valueShortUnicode(value.toSmile())

        val expected = byteArrayOf(
            0xAB.toByte(),
            *List(15 ) { listOf(0xE2.toByte(), 0x82.toByte(), 0xAC.toByte()) }.flatten().toByteArray()
        )

        val actual = builder.toByteArray()

        actual shouldBe expected
        sharedStorage.lookupValue(value) shouldNotBe null
    }

    @Test
    fun should_fail_short_unicode_for_too_long_string() {
        shouldThrow<IllegalArgumentException> {
            writer.valueShortUnicode(THREE_BYTE_CHAR.repeat(29).toSmile())
        }
    }

    @Test
    fun should_fail_short_unicode_for_too_short_string() {
        shouldThrow<IllegalArgumentException> {
            writer.valueShortUnicode(THREE_BYTE_CHAR.toSmile())
        }
    }


    @Test
    fun should_fail_short_unicode_for_non_unicode_string() {
        shouldThrow<IllegalArgumentException> {
            writer.valueShortUnicode("abcd".toSmile())
        }
    }
}

private const val THREE_BYTE_CHAR = "€"
