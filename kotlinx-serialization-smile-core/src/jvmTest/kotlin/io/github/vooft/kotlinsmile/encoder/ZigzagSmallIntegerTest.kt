package io.github.vooft.kotlinsmile.encoder

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Assertions.assertThrows

class ZigzagSmallIntegerTest : FunSpec({
    context("create instance for valid number") {
        withData(-16..15) {
            val zigzag = ZigzagSmallInteger.fromPlain(it)
            zigzag.toDecoded() shouldBe it.toByte()
            zigzag.toEncoded() shouldBe ZigzagInteger.encode(it).toByte()
        }
    }

    test("should fail to create instance for invalid number") {
        assertThrows(IllegalArgumentException::class.java) {
            ZigzagSmallInteger.fromPlain(-17)
        }
        assertThrows(IllegalArgumentException::class.java) {
            ZigzagSmallInteger.fromPlain(16)
        }
    }
})
