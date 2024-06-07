package io.github.vooft.kotlinsmile.common

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ZigzagIntegerTest {
    @Test
    fun should_encode_integer() {
        for (datum in testData) {
            ZigzagInteger.encode(datum.decoded) shouldBe datum.encoded
        }
    }

    @Test
    fun should_decode_integer() {
        for (datum in testData) {
            ZigzagInteger.decode(datum.encoded) shouldBe datum.decoded
        }
    }
}

private val testData = listOf(
    Zigzag(0, 0),
    Zigzag(1, -1),
    Zigzag(2, 1),
    Zigzag(3, -2),
    Zigzag(999, -500),
    Zigzag(Int.MAX_VALUE - 1, Int.MAX_VALUE / 2),
    Zigzag(Int.MAX_VALUE, Int.MIN_VALUE / 2),
)

private data class Zigzag(val encoded: Int, val decoded: Int)
