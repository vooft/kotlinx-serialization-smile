package io.github.vooft.kotlinsmile.common

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
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

class ZigzagSmallIntegerTest : FunSpec({
    context("create instance for valid number") {
        withData(-16..15) {
            val zigzag = ZigzagSmallInteger.fromPlain(it)
            zigzag.toDecoded() shouldBe it.toByte()
            zigzag.toEncoded() shouldBe ZigzagInteger.encode(it).toByte()
        }
    }

    context("should fail to create instance for invalid number") {
        withData(-17, 16, 100, -500) {
            shouldThrow<IllegalArgumentException> {
                ZigzagSmallInteger.fromPlain(it)
            }
        }
    }
})
