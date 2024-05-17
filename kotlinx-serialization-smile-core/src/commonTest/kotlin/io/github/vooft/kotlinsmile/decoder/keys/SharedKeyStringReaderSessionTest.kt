package io.github.vooft.kotlinsmile.decoder.keys

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifyNoMoreCalls
import io.github.vooft.kotlinsmile.common.ByteArrayIteratorImpl
import io.github.vooft.kotlinsmile.common.shared.SmileSharedStorage
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.random.Random
import kotlin.test.Test

class SharedKeyStringReaderSessionTest {

    private val mockStorage = mock<SmileSharedStorage>()

    @Test
    fun should_read_shared_key_0() {
        val data = byteArrayOf(
            0x40, // shared key with id = 0
        )

        val key = Random.nextLong().toString()
        every { mockStorage.getKeyById(0) } returns key

        val reader = SharedKeyStringReaderSession(ByteArrayIteratorImpl(data), mockStorage)
        reader.shortSharedKey() shouldBe key

        verify(VerifyMode.exhaustive) { mockStorage.getKeyById(0) }
    }

    @Test
    fun should_read_shared_key_5() {
        val data = byteArrayOf(
            0x45, // shared key with id = 5
        )

        val key = Random.nextLong().toString()
        every { mockStorage.getKeyById(5) } returns key

        val reader = SharedKeyStringReaderSession(ByteArrayIteratorImpl(data), mockStorage)
        reader.shortSharedKey() shouldBe key

        verify(VerifyMode.exhaustive) { mockStorage.getKeyById(5) }
    }

    @Test
    fun should_read_long_shared_key() {
        // encoding 0b1_0000_0001
        val data = byteArrayOf(
            0x31, // 0x30 offset + 1
            0b00000001.toByte(), // 0x01
        )

        val key = Random.nextLong().toString()
        every { mockStorage.getKeyById(257) } returns key

        val reader = SharedKeyStringReaderSession(ByteArrayIteratorImpl(data), mockStorage)
        reader.longSharedKey() shouldBe key

        verify(VerifyMode.exhaustive) { mockStorage.getKeyById(257) }
    }

    @Test
    fun should_fail_long_shared_key_when_too_small() {
        val data = byteArrayOf(
            0x30,
            0b00000001.toByte(), // 0x01
        )

        val reader = SharedKeyStringReaderSession(ByteArrayIteratorImpl(data), mockStorage)
        shouldThrow<IllegalArgumentException> { reader.longSharedKey() }

        verifyNoMoreCalls(mockStorage)
    }
}
