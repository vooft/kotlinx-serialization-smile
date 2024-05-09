package io.github.vooft.kotlinsmile.encoder.values

import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class SmallIntegerWriterSessionTest : FunSpec({

    context("should write small integer") {
        withData(
            1 to 0b110_00010u.toUByte(),
            15 to 0b110_11110u.toUByte(),
            -16 to 0b110_11111u.toUByte(),
        ) { (number, expected) ->
            val builder = ByteArrayBuilder()
            val session = SmallIntegerWriterSession(builder)

            session.smallInteger(number)

            val actualArray = builder.toByteArray()
            actualArray shouldHaveSize 1

            val actual = actualArray.single().toUByte()
            withClue("actual: ${actual.toString(2)}, expected: ${expected.toString(2)}") {
                actual shouldBe expected
            }
        }
    }

    context("should write small byte") {
        withData(
            1.toByte() to 0b110_00010u.toUByte(),
            15.toByte() to 0b110_11110u.toUByte(),
            (-16).toByte() to 0b110_11111u.toUByte(),
        ) { (number, expected) ->
            val builder = ByteArrayBuilder()
            val session = SmallIntegerWriterSession(builder)

            session.smallInteger(number)

            val actualArray = builder.toByteArray()
            actualArray shouldHaveSize 1

            val actual = actualArray.single().toUByte()
            withClue("actual: ${actual.toString(2)}, expected: ${expected.toString(2)}") {
                actual shouldBe expected
            }
        }
    }
})
