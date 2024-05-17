package io.github.vooft.kotlinsmile.encoder.keys

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

class SharedKeyStringWriterSessionTest {
    private val builder = ByteArrayBuilder()
    private val sharedStorage = mock<SmileSharedStorage> {  }

    private val session = SharedKeyStringWriterSession(builder, sharedStorage)

    @Test
    fun should_write_short_key() {
        val index = 10
        val key = Random.nextLong().toString()

        every { sharedStorage.lookupKey(key) } returns index

        session.keyShared(key)

        val expected = byteArrayOf(0x4A) // 0x40 offset + 10
        val actual = builder.toByteArray()

        actual shouldBe expected
    }

    @Test
    fun should_write_long_key_single_byte() {
        val index = 100
        val key = Random.nextLong().toString()

        every { sharedStorage.lookupKey(key) } returns index

        session.keyShared(key)

        val expected = byteArrayOf(0x30, 0x64) // 0x30 first byte + 100
        val actual = builder.toByteArray()

        actual shouldBe expected
    }

    @Test
    fun should_write_long_key_10_bits() {
        val index = 1000
        val key = Random.nextLong().toString()

        every { sharedStorage.lookupKey(key) } returns index

        session.keyShared(key)

        val expected = byteArrayOf(0x33, 0xE8.toByte()) // 0x30 first byte + 2 MSB
        val actual = builder.toByteArray()

        actual shouldBe expected
    }

    @Test
    fun should_fail_if_key_is_not_shared() {
        every { sharedStorage.lookupKey(any()) } returns null
        shouldThrow<IllegalArgumentException> { session.keyShared("test123") }
        builder.toByteArray() shouldBe byteArrayOf()
    }
}
