package io.github.vooft.kotlinsmile.common

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ZigzagIntegerTest : FunSpec({

    val data = listOf(
        Zigzag(0, 0),
        Zigzag(1, -1),
        Zigzag(2, 1),
        Zigzag(3, -2),
        Zigzag(999, -500),
        Zigzag(Int.MAX_VALUE - 1, Int.MAX_VALUE / 2),
        Zigzag(Int.MAX_VALUE, Int.MIN_VALUE / 2),
    )

    // there is an issue with kotest js and withData https://github.com/kotest/kotest/pull/3913
    for (datum in data) {
        test("should encode ${datum.decoded} to ${datum.encoded}") {
            ZigzagInteger.encode(datum.decoded) shouldBe datum.encoded
        }

        test("should decode ${datum.encoded} to ${datum.decoded}") {
            ZigzagInteger.decode(datum.encoded) shouldBe datum.decoded
        }
    }

//    context("should encode integer") {
//        withData(data) {
//            ZigzagInteger.encode(it.decoded) shouldBe it.encoded
//        }
//    }
//
//    context("should decode integer") {
//        withData(data) {
//            ZigzagInteger.decode(it.encoded) shouldBe it.decoded
//        }
//    }
})

private data class Zigzag(val encoded: Int, val decoded: Int)
