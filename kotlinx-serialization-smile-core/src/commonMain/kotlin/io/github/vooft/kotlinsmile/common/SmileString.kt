package io.github.vooft.kotlinsmile.common

data class SmileString(val value: String) {
    val encoded: ByteArray = value.encodeToByteArray()
}

val SmileString.isUnicode: Boolean get() = value.length != encoded.size
val SmileString.isAscii: Boolean get() = value.length == encoded.size
val SmileString.length: Int get() = encoded.size
val SmileString.isEmpty: Boolean get() = value.isEmpty()

fun String.toSmile() = SmileString(this)

fun SmileString.requireLength(range: IntRange) {
    require(length in range) { "Length must be in $range, actual: $length" }
}

fun SmileString.requireAscii() {
    require(!isUnicode) { "Only ASCII characters are allowed, actual: $value" }
}

fun SmileString.requireUnicode() {
    require(isUnicode) { "For only-ASCII strings need to use different method, actual: $value" }
}
