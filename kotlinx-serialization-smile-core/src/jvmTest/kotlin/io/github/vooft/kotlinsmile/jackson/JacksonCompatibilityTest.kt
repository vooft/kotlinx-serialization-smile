package io.github.vooft.kotlinsmile.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.smile.SmileFactory
import com.fasterxml.jackson.dataformat.smile.SmileGenerator
import io.github.vooft.kotlinsmile.encoder.SmileEncoderFactory
import io.github.vooft.kotlinsmile.smile.SmallInteger
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class JacksonCompatibilityTest : ShouldSpec({
    val smileMapper = ObjectMapper(
        SmileFactory.builder()
//            .configure(SmileGenerator.Feature.WRITE_HEADER, false)
            .configure(SmileGenerator.Feature.CHECK_SHARED_STRING_VALUES, false)
            .configure(SmileGenerator.Feature.CHECK_SHARED_NAMES, false)
            .build()
    )

    should("should serialize empty message") {
        val expected = smileMapper.writeValueAsBytes(1)
        println(expected.toBinaryString())
        println(expected.toHexString())

        println("0xC0 = " + 0xC0.toString(2))
        println("0xDF = " + 0xDF.toString(2))
        println(SmallInteger.mask.toString(2))
    }

    context("should serialize small integer") {
        val encoder = SmileEncoderFactory()

        withData(-16..15) {
            val expected = smileMapper.writeValueAsBytes(it)
            println(expected.toBinaryString())
            println(expected.toHexString())

            val actual = encoder.write {
                header()
                smallInteger(it)
            }

            actual shouldBe expected
        }
    }

})

private fun ByteArray.toHexString() = joinToString(", ", "[", "]") { it.toUByte().toString(16).padStart(2, '0') } + "]"
private fun ByteArray.toBinaryString() = joinToString(", ", "[", "]") { it.toUByte().toString(2).padStart(8, '0') }
