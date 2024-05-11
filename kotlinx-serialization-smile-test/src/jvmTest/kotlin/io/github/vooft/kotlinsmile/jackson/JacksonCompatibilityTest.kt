package io.github.vooft.kotlinsmile.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.smile.SmileFactory
import com.fasterxml.jackson.dataformat.smile.SmileGenerator
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.vooft.kotlinsmile.ObjWithSerializer
import io.github.vooft.kotlinsmile.Smile
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

    val basicTestCases = listOf(ObjWithSerializer(SimpleClass()))

    val structuralTestCases = listOf(
        ObjWithSerializer(CompositeObject()),
        ObjWithSerializer(ClassWithObjectsArray()),
        ObjWithSerializer(ClassWithObjectList()),
        ObjWithSerializer(ClassWithObjectSet()),
        ObjWithSerializer(ClassWithIntArray()),
        ObjWithSerializer(ClassWithIntList()),
        ObjWithSerializer(1, "root level int"),
        ObjWithSerializer("test", "root level string"),
        ObjWithSerializer(intArrayOf(1, 2, 3), "root level int array"),
        ObjWithSerializer(listOf(1, 2, 3), "root level int list"),
    )

    val keyTestCases = listOf(
        ObjWithSerializer(UnicodePropertyNameClass()),
        ObjWithSerializer(LongAsciiPropertyNameClass()),
        ObjWithSerializer(LongUnicodePropertyNameClass()),
        ObjWithSerializer(mapOf("a" to 1, "b" to 2), "root level map with string keys"),
        ObjWithSerializer(mapOf("1" to 1, "2" to 2), "root level map with int-as-string keys"),
        ObjWithSerializer(mapOf(1 to 1, 2 to 2), "root level map with int keys"), // int keys are encoded as strings
        ObjWithSerializer(mapOf(1L to 1, 2L to 2), "root level map with long keys"), // long keys are encoded as strings
        ObjWithSerializer(mapOf(1.toShort() to 1, 2.toShort() to 2), "root level map with short keys"), // short keys are encoded as strings
        ObjWithSerializer(mapOf(true to 1, false to 2), "root level map with boolean keys"), // boolean keys are encoded as strings
        ObjWithSerializer(mapOf('a' to 1, 'b' to 2), "root level map with char keys"), // char keys are encoded as strings
        ObjWithSerializer(mapOf(TestEnum.A to 1, TestEnum.B to 2), "root level map with enum keys"), // char keys are encoded as strings
        ObjWithSerializer(mapOf(1f to 1, 2f to 2), "root level map with float keys"), // char keys are encoded as strings
        ObjWithSerializer(mapOf(1.0 to 1, 2.0 to 2), "root level map with double keys"), // char keys are encoded as strings
    )

    val valueTestCases = listOf(
        ObjWithSerializer(TinyAsciiPropertyValueClass()),
        ObjWithSerializer(TinUnicodeyPropertyValueClass()),
        ObjWithSerializer(ShortAsciiPropertyValueClass()),
        ObjWithSerializer(ShortUnicodePropertyValueClass()),
        ObjWithSerializer(LongAsciiPropertyValueClass()),
        ObjWithSerializer(LongUnicodePropertyValueClass()),
        ObjWithSerializer(SimpleLiteralsClass()),
    )

    context("should serialize object same as jackson") {
        withData<ObjWithSerializer<*>>(
            nameFn = { it.name ?: it.obj!!::class.simpleName!! },
            ts = basicTestCases +
                    structuralTestCases +
                    keyTestCases +
                    valueTestCases
        ) {
            val expected = smileMapper.writeValueAsBytes(it.obj)

            println()
            logger.info { it.obj!!::class.simpleName }
            logger.info { "E: " + expected.toHexString() }

            val actual = Smile.encode(it)
            logger.info { "A: " + actual.toHexString() }
            println()

            actual shouldBe expected
        }
    }

    context("should deserialize object from jackson output") {
        withData<ObjWithSerializer<*>>(
            nameFn = { it.obj!!::class.simpleName!! },
            ts = basicTestCases +
                    structuralTestCases +
                    keyTestCases +
                    valueTestCases
        ) {
            val data = smileMapper.writeValueAsBytes(it.obj)

            val actual = Smile.decode(it.serializer, data)
            actual shouldBe it.obj
        }
    }
})

@Serializable
data class SimpleClass(val a: Int = 1, val bb: Int = 2)

@Serializable
data class CompositeObject(val a: Int = 1, val b: SimpleClass = SimpleClass())

@Serializable
data class TinyAsciiPropertyValueClass(val a: String = "test123")

@Serializable
data class TinUnicodeyPropertyValueClass(val a: String = "👨‍💼")

@Serializable
data class ShortAsciiPropertyValueClass(val a: String = "a".repeat(50))

@Serializable
data class ShortUnicodePropertyValueClass(val a: String = "👨‍💼".repeat(5))

@Serializable
data class LongAsciiPropertyValueClass(val a: String = "a".repeat(500))

@Serializable
data class LongUnicodePropertyValueClass(val a: String = "👨‍💼".repeat(50))

@Serializable
data class SimpleLiteralsClass(val e: String = "", val n: String? = null, val t: Boolean = true, val f: Boolean = false)

@Suppress("ConstructorParameterNaming")
@Serializable
data class UnicodePropertyNameClass(val `a👨‍💼`: Int = 1)

@Suppress("ConstructorParameterNaming")
@Serializable
data class LongUnicodePropertyNameClass(val `a👨‍💼👨‍💼👨‍💼👨‍💼👨‍💼👨‍💼👨‍💼👨‍💼👨‍💼👨‍💼`: Int = 1) // this is more than 100 bytes!

@Serializable
data class LongAsciiPropertyNameClass(val aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa: Int = 1)

@Serializable
data class ClassWithIntArray(val l: IntArray = intArrayOf(1, 2, 3)) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ClassWithIntArray

        return l.contentEquals(other.l)
    }

    override fun hashCode(): Int {
        return l.contentHashCode()
    }
}

@Serializable
data class ClassWithIntList(val l: List<Int> = listOf(1, 2, 3))


@Serializable
data class ClassWithObjectsArray(val l: Array<SimpleClass> = arrayOf(SimpleClass(), SimpleClass())) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ClassWithObjectsArray

        return l.contentEquals(other.l)
    }

    override fun hashCode(): Int {
        return l.contentHashCode()
    }
}

@Serializable
data class ClassWithObjectList(val l: List<SimpleClass> = listOf(SimpleClass(), SimpleClass()))

@Serializable
data class ClassWithObjectSet(val l: Set<SimpleClass> = setOf(SimpleClass(), SimpleClass()))

enum class TestEnum {
    A, B, C
}

private fun ByteArray.toHexString() = joinToString(", ", "[", "]") { it.toUByte().toString(16).padStart(2, '0') } + "]"

private val logger = KotlinLogging.logger { }
