package io.github.vooft.kotlinsmile.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.smile.SmileFactory
import com.fasterxml.jackson.dataformat.smile.SmileGenerator
import io.github.vooft.kotlinsmile.LargeSmileMessage
import io.github.vooft.kotlinsmile.Smile
import io.github.vooft.kotlinsmile.SmileConfig
import io.github.vooft.kotlinsmile.SmileEncoder
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import java.util.concurrent.ThreadLocalRandom

// TODO: reorganize
class JacksonCompatibilityTest : ShouldSpec({
    System.setProperty("kotest.assertions.collection.print.size", "10000")

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

    val hugeObject = listOf(ObjWithSerializer(LargeSmileMessage.next(), "huge object"))

    val objectsWithLotFieldsValues = listOf(
        ObjWithSerializer(ObjectWithTwoNestedFields()),
        ObjWithSerializer(ObjectWithTwoNestedLongAsciiFields()),
        ObjWithSerializer(ObjectWithTwoNestedLongUnicodeFields()),
        ObjWithSerializer(ObjectWithTwoNestedObjectsWithSameShortStringValues()),
        ObjWithSerializer(ObjectWithLargeNestedObjects()),
        ObjWithSerializer(ObjectWithRepeatedKeys()),
        ObjWithSerializer(ObjectWithRepeatedKeysAndValues()),
        ObjWithSerializer(ObjectWithFieldsFrom1To1000()),
        ObjWithSerializer(List(10) { ObjectWithFieldsFrom1To1000() }, "list with 10 objects with 1000 fields"),
        ObjWithSerializer(ObjectWithFieldsFrom1To2000())
    )

    val allTestCases = basicTestCases +
            structuralTestCases +
            keyTestCases +
            valueTestCases +
            hugeObject +
            objectsWithLotFieldsValues

    context("should serialize object same as jackson") {
        withData<ObjWithSerializer<*>>(
            nameFn = { it.name ?: it.obj!!::class.simpleName!! },
            ts = allTestCases
        ) {
            val expected = smileMapper.writeValueAsBytes(it.obj)

            val actual = Smile.encodeObjWithSerializer(it)

            actual shouldBe expected
        }
    }

    context("should deserialize object from jackson output") {
        withData<ObjWithSerializer<*>>(
            nameFn = { it.name ?: it.obj!!::class.simpleName!! },
            ts = allTestCases
        ) {
            val data = smileMapper.writeValueAsBytes(it.obj)
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

            val actual = Smile.encodeObjWithSerializer(it)

            withClue("E: " + expected.toHexString() + "\n" + "A: " + actual.toHexString()) {
                actual shouldBe expected
            }
        }
    }

    context("should serialize object with shared names") {
        val smileMapperWithSharedNames = ObjectMapper(
            SmileFactory.builder()
//            .configure(SmileGenerator.Feature.WRITE_HEADER, false)
                .configure(SmileGenerator.Feature.CHECK_SHARED_STRING_VALUES, false)
                .configure(SmileGenerator.Feature.CHECK_SHARED_NAMES, true)
                .build()
        ).findAndRegisterModules()

        val smile = Smile(SmileConfig(shareValues = false, shareKeys = true))

        withData<ObjWithSerializer<*>>(
            nameFn = { it.name ?: it.obj!!::class.simpleName!! },
            ts = allTestCases
        ) {
            val encoded = smileMapperWithSharedNames.writeValueAsBytes(it.obj)
            println(encoded.toHexString())
            println(encoded.toBinaryString())

            val actual = smile.encodeObjWithSerializer(it)
            println(actual.toHexString())
            println(actual.toBinaryString())

            actual.toHexString().replace(',', '\n') shouldBe encoded.toHexString().replace(',', '\n')
        }
    }

    context("should serialize object with shared values") {
        val smileMapperWithSharedNames = ObjectMapper(
            SmileFactory.builder()
//            .configure(SmileGenerator.Feature.WRITE_HEADER, false)
                .configure(SmileGenerator.Feature.CHECK_SHARED_STRING_VALUES, true)
                .configure(SmileGenerator.Feature.CHECK_SHARED_NAMES, false)
                .build()
        ).findAndRegisterModules()

        val smile = Smile(SmileConfig(shareValues = true, shareKeys = false))

        withData<ObjWithSerializer<*>>(
            nameFn = { it.name ?: it.obj!!::class.simpleName!! },
            ts = allTestCases
        ) {
            val encoded = smileMapperWithSharedNames.writeValueAsBytes(it.obj)
            println(encoded.toHexString())

            val actual = smile.encodeObjWithSerializer(it)
            println(actual.toHexString())

            actual.toHexString().replace(',', '\n') shouldBe encoded.toHexString().replace(',', '\n')
        }
    }

    context("should deserialize object with shared names") {
        val smileMapperWithSharedNames = ObjectMapper(
            SmileFactory.builder()
//            .configure(SmileGenerator.Feature.WRITE_HEADER, false)
                .configure(SmileGenerator.Feature.CHECK_SHARED_STRING_VALUES, false)
                .configure(SmileGenerator.Feature.CHECK_SHARED_NAMES, true)
                .build()
        ).findAndRegisterModules()

        withData<ObjWithSerializer<*>>(
            nameFn = { it.name ?: it.obj!!::class.simpleName!! },
            ts = allTestCases
        ) {
            val encoded = smileMapperWithSharedNames.writeValueAsBytes(it.obj)
            println(encoded.toHexString())

            val actual = Smile.decode(it.serializer, encoded)
            actual shouldBe it.obj
        }
    }

    context("should deserialize object with list with shared values") {
        val smileMapperWithSharedNames = ObjectMapper(
            SmileFactory.builder()
//            .configure(SmileGenerator.Feature.WRITE_HEADER, false)
                .configure(SmileGenerator.Feature.CHECK_SHARED_STRING_VALUES, true)
                .configure(SmileGenerator.Feature.CHECK_SHARED_NAMES, false)
                .build()
        ).findAndRegisterModules()

        withData(
            nameFn = { it.name ?: it.obj::class.simpleName!! },
            ts = listOf(
                ObjWithSerializer(ObjectWithList(32), "one long index"),
                ObjWithSerializer(ObjectWithList(100), "many long indexes"),
                ObjWithSerializer(ObjectWithList(255), "right on ignored value"),
                ObjWithSerializer(ObjectWithList(1000), "almost at overflow"),
                ObjWithSerializer(ObjectWithList(2000), "after overflow"),
            )
        ) {
            val encoded = smileMapperWithSharedNames.writeValueAsBytes(it.obj)
            println(encoded.toHexString())

            val actual = Smile.decode(it.serializer, encoded)
            withClue("list1") { actual.list1 shouldContainExactly it.obj.list1 }
            withClue("list2") { actual.list2 shouldContainExactly it.obj.list2 }
            withClue("list3") { actual.list3 shouldContainExactly it.obj.list3 }
            withClue("list4") { actual.list4 shouldContainExactly it.obj.list4 }
        }
    }

    context("should serialize object with shared names and values") {
        val smileMapperWithSharedNames = ObjectMapper(
            SmileFactory.builder()
//            .configure(SmileGenerator.Feature.WRITE_HEADER, false)
                .configure(SmileGenerator.Feature.CHECK_SHARED_STRING_VALUES, true)
                .configure(SmileGenerator.Feature.CHECK_SHARED_NAMES, true)
                .build()
        ).findAndRegisterModules()

        val smile = Smile(SmileConfig(shareValues = true, shareKeys = true))

        withData<ObjWithSerializer<*>>(
            nameFn = { it.name ?: it.obj!!::class.simpleName!! },
            ts = allTestCases
        ) {
            val encoded = smileMapperWithSharedNames.writeValueAsBytes(it.obj)
            println(encoded.toHexString())

            val actual = smile.encodeObjWithSerializer(it)
            println(actual.toHexString())

            actual.toHexString().replace(',', '\n') shouldBe encoded.toHexString().replace(',', '\n')
        }
    }

    context("should deserialize object with shared names and values") {
        val smileMapperWithSharedNames = ObjectMapper(
            SmileFactory.builder()
//            .configure(SmileGenerator.Feature.WRITE_HEADER, false)
                .configure(SmileGenerator.Feature.CHECK_SHARED_STRING_VALUES, true)
                .configure(SmileGenerator.Feature.CHECK_SHARED_NAMES, true)
                .build()
        ).findAndRegisterModules()

        withData<ObjWithSerializer<*>>(
            nameFn = { it.name ?: it.obj!!::class.simpleName!! },
            ts = allTestCases
        ) {
            val encoded = smileMapperWithSharedNames.writeValueAsBytes(it.obj)
            println(encoded.toHexString())

            val actual = Smile.decode(it.serializer, encoded)
            actual shouldBe it.obj
        }
    }
})

@Serializable
data class SimpleClass(val a: Int = 1, val bb: Int = 2)

@Serializable
data class CompositeObject(val a: Int = 1, val b: SimpleClass = SimpleClass())

@Serializable
data class ObjectWithTwoNestedFields(val a: SimpleClass = SimpleClass(), val b: SimpleClass = SimpleClass())

@Serializable
data class ObjectWithTwoNestedLongAsciiFields(
    val a: LongAsciiPropertyNameClass = LongAsciiPropertyNameClass(),
    val b: LongAsciiPropertyNameClass = LongAsciiPropertyNameClass()
)

@Serializable
data class ObjectWithTwoNestedLongUnicodeFields(
    val a: LongUnicodePropertyNameClass = LongUnicodePropertyNameClass(),
    val b: LongUnicodePropertyNameClass = LongUnicodePropertyNameClass()
)

@Serializable
data class ObjectWithTwoNestedObjectsWithSameShortStringValues(
    val field1: ObjectWithSameShortStringValues = ObjectWithSameShortStringValues(),
    val field2: ObjectWithSameShortStringValues = ObjectWithSameShortStringValues()
)

@Serializable
data class ObjectWithRepeatedKeys(
    val list: List<SimpleClass> = List(10) { SimpleClass() }
)

@Serializable
data class ObjectWithRepeatedKeysAndValues(
    val list: List<ObjectWithSameShortStringValues> = List(10) { ObjectWithSameShortStringValues() }
)

@Serializable
data class ObjectWithLargeNestedObjects(
    val obj1: ObjectWith100Fields = ObjectWith100Fields(),
    val obj2: ObjectWith100Fields = ObjectWith100Fields(),
)

@Serializable
data class ObjectWithList(
    val size: Int,
    val list1: List<String> = List(size) { "string-$it" },
    val list2: List<String> = list1,
    val list3: List<String> = list1,
    val list4: List<String> = list1
)

@Serializable
data class ObjectWith100Fields(
    val suffix: String = "suffix",
    val field0: String = "field0-$suffix",
    val field1: String = "field1-$suffix",
    val field2: String = "field2-$suffix",
    val field3: String = "field3-$suffix",
    val field4: String = "field4-$suffix",
    val field5: String = "field5-$suffix",
    val field6: String = "field6-$suffix",
    val field7: String = "field7-$suffix",
    val field8: String = "field8-$suffix",
    val field9: String = "field9-$suffix",
    val field10: String = "field10-$suffix",
    val field11: String = "field11-$suffix",
    val field12: String = "field12-$suffix",
    val field13: String = "field13-$suffix",
    val field14: String = "field14-$suffix",
    val field15: String = "field15-$suffix",
    val field16: String = "field16-$suffix",
    val field17: String = "field17-$suffix",
    val field18: String = "field18-$suffix",
    val field19: String = "field19-$suffix",
    val field20: String = "field20-$suffix",
    val field21: String = "field21-$suffix",
    val field22: String = "field22-$suffix",
    val field23: String = "field23-$suffix",
    val field24: String = "field24-$suffix",
    val field25: String = "field25-$suffix",
    val field26: String = "field26-$suffix",
    val field27: String = "field27-$suffix",
    val field28: String = "field28-$suffix",
    val field29: String = "field29-$suffix",
    val field30: String = "field30-$suffix",
    val field31: String = "field31-$suffix",
    val field32: String = "field32-$suffix",
    val field33: String = "field33-$suffix",
    val field34: String = "field34-$suffix",
    val field35: String = "field35-$suffix",
    val field36: String = "field36-$suffix",
    val field37: String = "field37-$suffix",
    val field38: String = "field38-$suffix",
    val field39: String = "field39-$suffix",
    val field40: String = "field40-$suffix",
    val field41: String = "field41-$suffix",
    val field42: String = "field42-$suffix",
    val field43: String = "field43-$suffix",
    val field44: String = "field44-$suffix",
    val field45: String = "field45-$suffix",
    val field46: String = "field46-$suffix",
    val field47: String = "field47-$suffix",
    val field48: String = "field48-$suffix",
    val field49: String = "field49-$suffix",
    val field50: String = "field50-$suffix",
    val field51: String = "field51-$suffix",
    val field52: String = "field52-$suffix",
    val field53: String = "field53-$suffix",
    val field54: String = "field54-$suffix",
    val field55: String = "field55-$suffix",
    val field56: String = "field56-$suffix",
    val field57: String = "field57-$suffix",
    val field58: String = "field58-$suffix",
    val field59: String = "field59-$suffix",
    val field60: String = "field60-$suffix",
    val field61: String = "field61-$suffix",
    val field62: String = "field62-$suffix",
    val field63: String = "field63-$suffix",
    val field64: String = "field64-$suffix",
    val field65: String = "field65-$suffix",
    val field66: String = "field66-$suffix",
    val field67: String = "field67-$suffix",
    val field68: String = "field68-$suffix",
    val field69: String = "field69-$suffix",
    val field70: String = "field70-$suffix",
    val field71: String = "field71-$suffix",
    val field72: String = "field72-$suffix",
    val field73: String = "field73-$suffix",
    val field74: String = "field74-$suffix",
    val field75: String = "field75-$suffix",
    val field76: String = "field76-$suffix",
    val field77: String = "field77-$suffix",
    val field78: String = "field78-$suffix",
    val field79: String = "field79-$suffix",
    val field80: String = "field80-$suffix",
    val field81: String = "field81-$suffix",
    val field82: String = "field82-$suffix",
    val field83: String = "field83-$suffix",
    val field84: String = "field84-$suffix",
    val field85: String = "field85-$suffix",
    val field86: String = "field86-$suffix",
    val field87: String = "field87-$suffix",
    val field88: String = "field88-$suffix",
    val field89: String = "field89-$suffix",
    val field90: String = "field90-$suffix",
    val field91: String = "field91-$suffix",
    val field92: String = "field92-$suffix",
    val field93: String = "field93-$suffix",
    val field94: String = "field94-$suffix",
    val field95: String = "field95-$suffix",
    val field96: String = "field96-$suffix",
    val field97: String = "field97-$suffix",
    val field98: String = "field98-$suffix",
    val field99: String = "field99-$suffix",
    val field100: String = "field100-$suffix"
)

@Serializable
data class ObjectWithSameShortStringValues(val field1: String = "test123", val field2: String = "👨‍💼")

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

class ObjWithSerializer<T>(val obj: T, val serializer: KSerializer<T>, val name: String?)
inline fun <reified T> ObjWithSerializer(obj: T, name: String? = null) = ObjWithSerializer(obj, serializer<T>(), name)

fun <T> SmileEncoder.encodeObjWithSerializer(obj: ObjWithSerializer<T>) = encode(obj.serializer, obj.obj)
