package io.github.vooft.kotlinsmile.encoder.values

import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class IntegerWriterSessionTest : FunSpec({

    context("should write small integer") {
        withData(
            1 to 0b110_00010u.toUByte(),
            15 to 0b110_11110u.toUByte(),
            -16 to 0b110_11111u.toUByte(),
        ) { (number, expected) ->
            val builder = ByteArrayBuilder()
            val session = IntegerWriterSession(builder)

            session.valueSmallInteger(number)

            val actualArray = builder.toByteArray()
            actualArray shouldHaveSize 1

            val actual = actualArray.single().toUByte()
            withClue("actual: ${actual.toString(2)}, expected: ${expected.toString(2)}") {
                actual shouldBe expected
            }
        }
    }

    context("should write regular integers") {
        withData(
            100 to byteArrayOf(1),
            1500 to byteArrayOf(2),
            -16000 to byteArrayOf(3),
        ) { (number, expected) ->
            val builder = ByteArrayBuilder()
            val session = IntegerWriterSession(builder)

            session.valueRegularInteger(number)

            val actualArray = builder.toByteArray()
            actualArray shouldBe expected
        }
    }
})
