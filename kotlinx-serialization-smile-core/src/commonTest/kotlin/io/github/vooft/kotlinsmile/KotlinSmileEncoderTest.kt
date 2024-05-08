package io.github.vooft.kotlinsmile

import io.kotest.matchers.collections.shouldContainExactly
import kotlin.test.Test

class KotlinSmileEncoderTest {
    @Test
    fun `should encode simple object`() {
        val result = encodeToList(1)
        result shouldContainExactly listOf(1)
    }
}
