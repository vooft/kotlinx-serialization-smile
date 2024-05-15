package io.github.vooft.kotlinsmile.decoder.values

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode
import io.github.vooft.kotlinsmile.common.ByteArrayIteratorImpl
import io.github.vooft.kotlinsmile.decoder.shared.DecodingSmileSharedStorage
import io.kotest.matchers.shouldBe
import kotlin.random.Random
import kotlin.test.Test

class SharedStringReaderSessionTest {

    private val mockStorage = mock<DecodingSmileSharedStorage>()

    @Test
    fun should_read_shared_key_0() {
        val data = byteArrayOf(
            0x40, // shared key with id = 0
        )

        val key = Random.nextLong().toString()
        every { mockStorage.getKey(0) } returns key

        val reader = SharedStringReaderSession(ByteArrayIteratorImpl(data), mockStorage)
        reader.shortSharedKey() shouldBe key

        verify(VerifyMode.exhaustive) { mockStorage.getKey(0) }
    }

    @Test
    fun should_read_shared_key_5() {
        val data = byteArrayOf(
            0x45, // shared key with id = 5
        )

        val key = Random.nextLong().toString()
        every { mockStorage.getKey(5) } returns key

        val reader = SharedStringReaderSession(ByteArrayIteratorImpl(data), mockStorage)
        reader.shortSharedKey() shouldBe key

        verify(VerifyMode.exhaustive) { mockStorage.getKey(5) }
    }
}
