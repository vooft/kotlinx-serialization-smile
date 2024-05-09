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
    ).findAndRegisterModules()

    should("should serialize empty message") {
        val expected = smileMapper.writeValueAsBytes(1)
        println(expected.toBinaryString())
        println(expected.toHexString())

        printlnUByte(0xC0u)
        printlnUByte(0xDFu)
        printlnUByte(0x20u)
        printlnUByte(0x21u)
        printlnUByte(0x22u)
        printlnUByte(0x23u)
        printlnUByte(0x3Fu)
        printlnUByte(0xF8u)
        printlnUByte(0xF9u)
        printlnUByte(0xFAu)
        printlnUByte(0xFBu)
        printlnUByte(0x80u)
        printlnUByte(0xBFu)
        printlnUByte(0x9Fu)
        printlnUByte(0x40u)
        printlnUByte(0x5Fu)
        println(SmallInteger.offset.toString(2).padStart(8, '0'))
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

    should("serialize simple object") {
        val obj = TestObject(1, 2)

        val expected = smileMapper.writeValueAsBytes(obj)
        println(expected.toBinaryString())
        println(expected.toHexString())

        val encoder = SmileEncoderFactory()
        val actual = encoder.write {
            header()
            startObject()
            keyShortAscii("a")
            smallInteger(obj.a)
            keyShortAscii("bb")
            smallInteger(obj.bb)
            endObject()
        }
        println(actual.toBinaryString())
        println(actual.toHexString())

        actual shouldBe expected
    }

    should("bla") {
        println(byteArrayOf(0x20, 0x21, 0x22).toBinaryString())
    }

})

//data class TestObject(val aa: Int, val `a👨‍💼`: Int)
data class TestObject(val a: Int, val bb: Int)

private fun printlnUByte(uByte: UByte) {
    println("0x" + uByte.toString(16).uppercase().padStart(2, '0') + " = " + uByte.toString(2).padStart(8, '0'))
}

private fun ByteArray.toHexString() = joinToString(", ", "[", "]") { it.toUByte().toString(16).padStart(2, '0') } + "]"
private fun ByteArray.toBinaryString() = joinToString(", ", "[", "]") { it.toUByte().toString(2).padStart(8, '0') }
