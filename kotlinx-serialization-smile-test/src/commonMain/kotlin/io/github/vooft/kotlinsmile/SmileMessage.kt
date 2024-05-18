package io.github.vooft.kotlinsmile

import io.github.vooft.kotlinsmile.token.SmileValueToken.LongInteger
import io.github.vooft.kotlinsmile.token.SmileValueToken.RegularInteger
import io.github.vooft.kotlinsmile.token.SmileValueToken.ShortAscii
import io.github.vooft.kotlinsmile.token.SmileValueToken.ShortUnicode
import io.github.vooft.kotlinsmile.token.SmileValueToken.SmallInteger
import io.github.vooft.kotlinsmile.token.SmileValueToken.TinyAscii
import io.github.vooft.kotlinsmile.token.SmileValueToken.TinyUnicode
import kotlinx.serialization.Serializable
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.random.nextLong

@Serializable
data class SmileMessage(
    val smallByte: Byte,
    val regularInt: Int,
    val longInt: Long,
    val tinyAscii: String,
    val tinyUnicode: String,
    val shortAscii: String,
    val shortUnicode: String,
    val longAscii: String,
    val veryLongAscii: String,
    val longUnicode: String,
    val veryLongUnicode: String,
    val float: Float,
    val double: Double,
    val binary: ByteArrayWrapper,
    val nestedObject: NestedObject,
    val listOfObjects: List<NestedObject>,
    val stringMapOfObjects: Map<String, NestedObject>,
    val intMapOfObjects: Map<Int, NestedObject>,
    val emptyString: String,
    val nullValue: String?,
    val boolean: Boolean
) {
    companion object {
        fun next() = SmileMessage(
            smallByte = Random.nextInt(SmallInteger.values).toByte(),
            regularInt = Random.nextInt(RegularInteger.values),
            longInt = Random.nextLong(LongInteger.values),
            tinyAscii = Random.nextAscii(TinyAscii.lengths.last),
            tinyUnicode = Random.nextUnicode(TinyUnicode.lengths.last),
            shortAscii = Random.nextAscii(ShortAscii.lengths.last),
            shortUnicode = Random.nextUnicode(ShortUnicode.lengths.last),
            longAscii = Random.nextAscii(ShortAscii.lengths.last + 10),
            veryLongAscii = Random.nextAscii(ShortAscii.lengths.last + 200),
            longUnicode = Random.nextUnicode(ShortUnicode.lengths.last + 10),
            veryLongUnicode = Random.nextUnicode(ShortUnicode.lengths.last + 200),
            float = Random.nextFloat(),
            double = Random.nextDouble(),
            binary = ByteArrayWrapper(Random.nextBytes(Random.nextInt(100..200))),
            nestedObject = NestedObject.next(),
            listOfObjects = List(10) { NestedObject.next() },
            stringMapOfObjects = mapOf(
                Random.nextUnicode(10) to NestedObject.next(),
                Random.nextAscii(10) to NestedObject.next()
            ),
            intMapOfObjects = mapOf(
                Random.nextInt(100) to NestedObject.next(),
                Random.nextInt(100) to NestedObject.next()
            ),
            emptyString = "",
            nullValue = null,
            boolean = Random.nextBoolean()
        )
    }
}

@Serializable
data class NestedObject(val username: String, val url: String, val userAgent: String) {
    companion object {
        fun next() = NestedObject(Random.nextAscii(10), Random.nextUnicode(20), Random.nextAscii(100))
    }
}

@Serializable
class ByteArrayWrapper(val bytes: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        val casted = other as? ByteArrayWrapper ?: return false
        return bytes.contentEquals(casted.bytes)
    }

    override fun hashCode(): Int {
        return bytes.contentHashCode()
    }
}

private fun Random.nextAscii(length: Int): String {
    val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    return (1..length)
        .map { source.random() }
        .joinToString("")
}

private fun Random.nextUnicode(length: Int): String {
    val source = listOf(
        "😀", "😃", "😄", "😁", "😆", "😅", "😂", "🤣", "😭", "😗",
        "😘", "😍", "😉", "😊", "😋", "😎", "😏", "😇", "😈", "😺",
        "😸", "😹", "😻", "😼", "😽", "🙀", "😿", "😾", "👹", "👺",
        "🤡", "👻", "💀", "👽", "👾", "🤖", "🎃"
    )
    return (1..length)
        .map { source.random() }
        .joinToString("")
}

