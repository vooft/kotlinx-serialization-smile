package io.github.vooft.kotlinsmile.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.smile.SmileFactory
import com.fasterxml.jackson.dataformat.smile.SmileGenerator
import io.github.vooft.kotlinsmile.ObjWithSerializer
import io.github.vooft.kotlinsmile.Smile
import io.github.vooft.kotlinsmile.token.SmileValueToken.SmallInteger
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable

class JacksonCompatibilityTest : ShouldSpec({
    System.setProperty("kotest.assertions.collection.print.size", "1000")

    val smileMapper = ObjectMapper(
        SmileFactory.builder()
//            .configure(SmileGenerator.Feature.WRITE_HEADER, false)
            .configure(SmileGenerator.Feature.CHECK_SHARED_STRING_VALUES, false)
            .configure(SmileGenerator.Feature.CHECK_SHARED_NAMES, false)
            .build()
    ).findAndRegisterModules()

    context("should serialize object same as jackson") {
        withData(
            listOf(
                ObjWithSerializer(TestObject(1, 2)),
                ObjWithSerializer(CompositeObject(5, TestObject(6, 7))),
//                ObjWithSerializer(UnicodePropertyObject(2)),
                ObjWithSerializer(LongPropertyName(3))
            )
        ) {
            val expected = smileMapper.writeValueAsBytes(it.obj)
            println(expected.toBinaryString())
            println(expected.toHexString())

            val actual = Smile.encode(it)
            println(actual.toBinaryString())
            println(actual.toHexString())

            actual shouldBe expected
        }
    }

    should("bla") {
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
})

//@Suppress("ConstructorParameterNaming")
//@Serializable
//data class UnicodePropertyObject(val `a👨‍💼`: Int)

@Serializable
data class LongPropertyName(val aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa: Int)

@Serializable
data class TestObject(val a: Int, val bb: Int)

@Serializable
data class CompositeObject(val a: Int, val b: TestObject)

private fun printlnUByte(uByte: UByte) {
    println("0x" + uByte.toString(16).uppercase().padStart(2, '0') + " = " + uByte.toString(2).padStart(8, '0'))
}

private fun ByteArray.toHexString() = joinToString(", ", "[", "]") { it.toUByte().toString(16).padStart(2, '0') } + "]"
private fun ByteArray.toBinaryString() = joinToString(", ", "[", "]") { it.toUByte().toString(2).padStart(8, '0') }
