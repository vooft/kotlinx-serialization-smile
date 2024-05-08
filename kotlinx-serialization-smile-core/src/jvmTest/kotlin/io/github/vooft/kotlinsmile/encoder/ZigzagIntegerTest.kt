package io.github.vooft.kotlinsmile.encoder

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class ZigzagIntegerTest : ShouldSpec({
    context("should encode small integer") {
        withData(
            listOf(
                0 to 0,
                -1 to 1,
                1 to 2,
                -2 to 3,
                -500 to 999,
                Int.MAX_VALUE / 2 to Int.MAX_VALUE - 1,
                Int.MIN_VALUE / 2 to Int.MAX_VALUE,
            )
        ) { (plain, encoded) ->
            ZigzagInteger.encode(plain) shouldBe encoded
            ZigzagInteger.decode(encoded) shouldBe plain
        }
    }
})
