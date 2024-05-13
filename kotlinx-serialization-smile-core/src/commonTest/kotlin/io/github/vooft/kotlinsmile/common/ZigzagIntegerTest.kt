package io.github.vooft.kotlinsmile.common

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class ZigzagIntegerTest : ShouldSpec({

    val data = listOf(
        Zigzag(0, 0),
        Zigzag(1, -1),
        Zigzag(2, 1),
        Zigzag(3, -2),
        Zigzag(999, -500),
        Zigzag(Int.MAX_VALUE - 1, Int.MAX_VALUE / 2),
        Zigzag(Int.MAX_VALUE, Int.MIN_VALUE / 2),
    )

    context("should encode integer") {
        withData(data) {
            ZigzagInteger.encode(it.decoded) shouldBe it.encoded
        }
    }

    context("should decode integer") {
        withData(data) {
            ZigzagInteger.decode(it.encoded) shouldBe it.decoded
        }
    }
})

private data class Zigzag(val encoded: Int, val decoded: Int)
