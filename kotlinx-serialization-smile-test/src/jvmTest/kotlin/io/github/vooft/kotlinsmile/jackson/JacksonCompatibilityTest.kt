package io.github.vooft.kotlinsmile.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.smile.SmileFactory
import com.fasterxml.jackson.dataformat.smile.SmileGenerator
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.vooft.kotlinsmile.ObjWithSerializer
import io.github.vooft.kotlinsmile.Smile
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import java.util.concurrent.ThreadLocalRandom

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
        ObjWithSerializer(ClassWithByteArray()),
        ObjWithSerializer(ClassWithRandomByteArray()),
        ObjWithSerializer(1, "root level small int"),
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
        ObjWithSerializer(SimpleClass(a = Short.MAX_VALUE.toInt(), bb = Short.MIN_VALUE.toInt()), "short edges"),
        ObjWithSerializer(TinyAsciiPropertyValueClass()),
        ObjWithSerializer(TinUnicodeyPropertyValueClass()),
        ObjWithSerializer(ShortAsciiPropertyValueClass()),
        ObjWithSerializer(ShortUnicodePropertyValueClass()),
        ObjWithSerializer(LongAsciiPropertyValueClass()),
        ObjWithSerializer(LongUnicodePropertyValueClass()),
        ObjWithSerializer(SimpleLiteralsClass()),
        ObjWithSerializer(CharPropertyClass()),
        ObjWithSerializer(ClassWithEnumValues()),
        ObjWithSerializer(ClassWithFloatProperty()),
        ObjWithSerializer(ClassWithDoubleProperty()),
        ObjWithSerializer(100, "root level regular positive int"),
        ObjWithSerializer(-100, "root level regular negative int"),
        ObjWithSerializer(Int.MAX_VALUE, "root level max int"),
        ObjWithSerializer(Int.MIN_VALUE, "root level min int"),
        ObjWithSerializer(1L, "root level small long"),
        ObjWithSerializer(100L, "root level regular positive long"),
        ObjWithSerializer(-100L, "root level regular negative long"),
        ObjWithSerializer(Long.MAX_VALUE, "root level max long"),
        ObjWithSerializer(Long.MIN_VALUE, "root level min long"),
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
            logger.info { it.name ?: it.obj!!::class.simpleName!! }
            logger.info { "E: " + expected.toHexString() }
            logger.info { "E: " + expected.toBinaryString() }

            val actual = Smile.encode(it)
            logger.info { "A: " + actual.toHexString() }
            logger.info { "A: " + actual.toBinaryString() }
            println()

            actual shouldBe expected
        }
    }

    context("should deserialize object from jackson output") {
        withData<ObjWithSerializer<*>>(
            nameFn = { it.name ?: it.obj!!::class.simpleName!! },
            ts = basicTestCases +
                    structuralTestCases +
                    keyTestCases +
                    valueTestCases
        ) {
            val data = smileMapper.writeValueAsBytes(it.obj)

            logger.info { "D: " + data.toHexString() }

            val actual = Smile.decode(it.serializer, data)
            actual shouldBe it.obj
        }
    }

    context("should deserialize object with random byte array") {
        withData(
            nameFn = { "random byte array with length ${it.obj.b.size}" },
            ts = List(100) {
                val array = ByteArray(ThreadLocalRandom.current().nextInt(10_000)).apply { ThreadLocalRandom.current().nextBytes(this) }
                ObjWithSerializer(ClassWithRandomByteArray(b = array))
            }
        ) {
            val expected = smileMapper.writeValueAsBytes(it.obj)

            val actual = Smile.encode(it)

            withClue("E: " + expected.toHexString() + "\n" + "A: " + actual.toHexString()) {
                actual shouldBe expected
            }
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

@Serializable
data class CharPropertyClass(val a: Char = 'a')

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
data class ClassWithEnumValues(val a: TestEnum = TestEnum.A, val b: TestEnum = TestEnum.B)

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

@Serializable
data class ClassWithFloatProperty(val f: Float = 1.0f)

@Serializable
data class ClassWithDoubleProperty(val d: Double = 1.0)

@Serializable
data class ClassWithByteArray(val b: ByteArray = byteArrayOf(3, 2, 1)) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ClassWithByteArray

        return b.contentEquals(other.b)
    }

    override fun hashCode(): Int {
        return b.contentHashCode()
    }
}

@Serializable
data class ClassWithRandomByteArray(val b: ByteArray = ByteArray(1000).apply { ThreadLocalRandom.current().nextBytes(this) }) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ClassWithRandomByteArray

        return b.contentEquals(other.b)
    }

    override fun hashCode(): Int {
        return b.contentHashCode()
    }
}

enum class TestEnum {
    A, B, C
}

private fun ByteArray.toHexString() = joinToString(", ", "[", "]") { it.toUByte().toString(16).padStart(2, '0') } + "]"
private fun ByteArray.toBinaryString() = joinToString(", ", "[", "]") { it.toUByte().toString(2).padStart(8, '0') }

private val logger = KotlinLogging.logger { }
