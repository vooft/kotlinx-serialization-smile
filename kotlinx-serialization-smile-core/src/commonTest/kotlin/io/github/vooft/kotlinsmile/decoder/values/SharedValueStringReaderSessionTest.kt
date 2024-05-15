package io.github.vooft.kotlinsmile.decoder.values

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode
import io.github.vooft.kotlinsmile.common.ByteArrayIteratorImpl
import io.github.vooft.kotlinsmile.decoder.shared.DecodingSmileSharedStorage
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.random.Random
import kotlin.test.Test

class SharedValueStringReaderSessionTest {
    private val mockStorage = mock<DecodingSmileSharedStorage>()

    @Test
    fun should_fail_to_read_shared_value_0() {
        val session = SharedValueStringReaderSession(ByteArrayIteratorImpl(byteArrayOf(0x00)), mockStorage)
        shouldThrow<InvalidSmileSpecImplementationException> { 
            session.shortSharedValue()
        }
    }
    
    @Test
    fun should_read_shared_value_1() {
        val data = byteArrayOf(
            0x01,
        )

        val key = Random.nextLong().toString()
        every { mockStorage.getValue(0) } returns key // because index starts with 1

        val reader = SharedValueStringReaderSession(ByteArrayIteratorImpl(data), mockStorage)
        reader.shortSharedValue() shouldBe key

        verify(VerifyMode.exhaustive) { mockStorage.getValue(0) }
    }

    @Test
    fun should_read_shared_key_5() {
        val data = byteArrayOf(
            0x05,
        )

        val key = Random.nextLong().toString()
        every { mockStorage.getValue(4) } returns key  // because index starts with 1

        val reader = SharedValueStringReaderSession(ByteArrayIteratorImpl(data), mockStorage)
        reader.shortSharedValue() shouldBe key

        verify(VerifyMode.exhaustive) { mockStorage.getValue(4) }
    }

//    @Test
//    fun should_read_long_shared_key() {
//        // encoding 0b1_0000_0001
//        val data = byteArrayOf(
//            0x31, // 0x30 offset + 1
//            0b00000001.toByte(), // 0x01
//        )
//
//        val key = Random.nextLong().toString()
//        every { mockStorage.getKey(257) } returns key
//
//        val reader = SharedValueStringReaderSession(ByteArrayIteratorImpl(data), mockStorage)
//        reader.longSharedKey() shouldBe key
//
//        verify(VerifyMode.exhaustive) { mockStorage.getKey(257) }
//    }
}
