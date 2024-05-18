package io.github.vooft.kotlinsmile.decoder.raw

import io.github.vooft.kotlinsmile.common.ByteArrayIteratorImpl
import io.github.vooft.kotlinsmile.token.SmileMarkers
import io.kotest.matchers.shouldBe
import kotlin.random.Random
import kotlin.test.Test

class RawLongStringReaderTest {
    @Test
    fun should_read_raw_long_string() {
        val expected = Random.nextLong().toString()

        val data = byteArrayOf(
            *expected.encodeToByteArray(),
            SmileMarkers.STRING_END_MARKER
        )

        val iterator = ByteArrayIteratorImpl(data)
        val result = iterator.nextRawLongString()

        result shouldBe expected
    }
}
