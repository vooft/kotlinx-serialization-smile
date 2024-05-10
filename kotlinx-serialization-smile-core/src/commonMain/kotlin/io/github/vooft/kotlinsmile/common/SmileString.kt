package io.github.vooft.kotlinsmile.common

data class SmileString(val value: String) {
    val encoded: ByteArray = value.encodeToByteArray()
}

val SmileString.isUnicode: Boolean get() = value.length != encoded.size
val SmileString.isAscii: Boolean get() = value.length == encoded.size
val SmileString.byteLength: Int get() = encoded.size

fun String.toSmile() = SmileString(this)

fun SmileString.requireLength(range: IntRange) {
    require(byteLength in range) { "Length must be in $range, actual: $byteLength" }
}

fun SmileString.requireAscii() {
    require(!isUnicode) { "Only ASCII characters are allowed, actual: $value" }
}

fun SmileString.requireUnicode() {
    require(isUnicode) { "For only-ASCII strings need to use different method, actual: $value" }
}
