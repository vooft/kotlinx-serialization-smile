package io.github.vooft.kotlinsmile.encoder.values

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.github.vooft.kotlinsmile.common.shared.SmileSharedStorage
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.random.Random
import kotlin.test.Test

class SharedValueShortStringWriterSessionTest {
    private val builder = ByteArrayBuilder()
    private val sharedStorage = mock<SmileSharedStorage> {  }

    private val session = SharedValueShortStringWriterSession(builder, sharedStorage)

    @Test
    fun should_write_short_value() {
        val index = 10
        val value = Random.nextLong().toString()

        every { sharedStorage.lookupValue(value) } returns index

        session.valueShared(value)

        val expected = byteArrayOf(0x0B) // 0 is not used, 0x1 + 10
        val actual = builder.toByteArray()

        actual shouldBe expected
    }

    @Test
    fun should_write_long_value_single_byte() {
        val index = 100
        val value = Random.nextLong().toString()

        every { sharedStorage.lookupValue(value) } returns index

        session.valueShared(value)

        val expected = byteArrayOf(0xEC.toByte(), 0x64) // 0xEC first byte + 100
        val actual = builder.toByteArray()

        actual shouldBe expected
    }

    @Test
    fun should_write_long_value_10_bits() {
        val index = 1000
        val value = Random.nextLong().toString()

        every { sharedStorage.lookupValue(value) } returns index

        session.valueShared(value)

        val expected = byteArrayOf(0xEF.toByte(), 0xE8.toByte()) // 0xEC first byte + 2 MSB
        val actual = builder.toByteArray()

        actual shouldBe expected
    }

    @Test
    fun should_fail_if_value_is_not_shared() {
        every { sharedStorage.lookupValue(any()) } returns null
        shouldThrow<IllegalArgumentException> { session.valueShared("test123") }
        builder.toByteArray() shouldBe byteArrayOf()
    }
}
