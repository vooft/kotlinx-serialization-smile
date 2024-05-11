package io.github.vooft.kotlinsmile.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.smile.SmileFactory
import com.fasterxml.jackson.dataformat.smile.SmileGenerator
import io.github.oshai.kotlinlogging.KotlinLogging
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

    val testData = listOf(
        ObjWithSerializer(TestObject()),
        ObjWithSerializer(CompositeObject()),
        ObjWithSerializer(UnicodePropertyObject()),
        ObjWithSerializer(LongPropertyName()),
        ObjWithSerializer(UnicodeLongPropertyName()),
        ObjWithSerializer(AsciiTinyPropertyValue()),
        ObjWithSerializer(UnicodeTinyPropertyValue()),
        ObjWithSerializer(AsciiShortPropertyValue()),
        ObjWithSerializer(UnicodeShortPropertyValue()),
        ObjWithSerializer(AsciiLongPropertyValue()),
        ObjWithSerializer(UnicodeLongPropertyValue()),
        ObjWithSerializer(SimpleLiteralObject()),
        ObjWithSerializer(ClassWithObjectsArray()),
    )

    context("should serialize object same as jackson") {
        withData<ObjWithSerializer<*>>(
            nameFn = { it.obj!!::class.simpleName!! },
            ts = testData
        ) {
            val expected = smileMapper.writeValueAsBytes(it.obj)

            val actual = Smile.encode(it)

            println()
            logger.info { it.obj!!::class.simpleName }
            logger.info { expected.toBinaryString() }
            logger.info { actual.toBinaryString() }

            logger.info { expected.toHexString() }
            logger.info { actual.toHexString() }
            println()

            actual shouldBe expected
        }
    }

    context("should deserialize object from jackson output") {
        withData<ObjWithSerializer<*>>(
            nameFn = { it.obj!!::class.simpleName!! },
            ts = testData
        ) {
            val data = smileMapper.writeValueAsBytes(it.obj)

            val actual = Smile.decode(it.serializer, data)
            actual shouldBe it.obj
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
        logger.info { SmallInteger.offset.toString(2).padStart(8, '0') }
    }
})

@Suppress("ConstructorParameterNaming")
@Serializable
data class UnicodePropertyObject(val `a👨‍💼`: Int = 1)

@Suppress("ConstructorParameterNaming")
@Serializable
data class UnicodeLongPropertyName(val `a👨‍💼👨‍💼👨‍💼👨‍💼👨‍💼👨‍💼👨‍💼👨‍💼👨‍💼👨‍💼`: Int = 1) // this is more than 100 bytes!

@Serializable
data class LongPropertyName(val aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa: Int = 1)

@Serializable
data class TestObject(val a: Int = 1, val bb: Int = 2)

@Serializable
data class CompositeObject(val a: Int = 1, val b: TestObject = TestObject())

@Serializable
data class AsciiTinyPropertyValue(val a: String = "test123")

@Serializable
data class UnicodeTinyPropertyValue(val a: String = "👨‍💼")

@Serializable
data class AsciiShortPropertyValue(val a: String = "a".repeat(50))

@Serializable
data class UnicodeShortPropertyValue(val a: String = "👨‍💼".repeat(5))

@Serializable
data class AsciiLongPropertyValue(val a: String = "a".repeat(500))

@Serializable
data class UnicodeLongPropertyValue(val a: String = "👨‍💼".repeat(50))

@Serializable
data class SimpleLiteralObject(val e: String = "", val n: String? = null, val t: Boolean = true, val f: Boolean = false)

@Serializable
data class ClassWithObjectsArray(val l: Array<TestObject> = arrayOf(TestObject(), TestObject()))

private fun printlnUByte(uByte: UByte) {
    logger.info { "0x" + uByte.toString(16).uppercase().padStart(2, '0') + " = " + uByte.toString(2).padStart(8, '0') }
}

private fun ByteArray.toHexString() = joinToString(", ", "[", "]") { it.toUByte().toString(16).padStart(2, '0') } + "]"
private fun ByteArray.toBinaryString() = joinToString(", ", "[", "]") { it.toUByte().toString(2).padStart(8, '0') }

private val logger = KotlinLogging.logger {  }
