package io.github.vooft.kotlinsmile.common

import kotlinx.io.bytestring.ByteStringBuilder

interface ByteArrayBuilder {
    fun append(byte: Byte, offset: Byte = 0)
    fun append(array: ByteArray)

    fun toByteArray(): ByteArray
}

fun ByteArrayBuilder(initialCapacity: Int = 1024): ByteArrayBuilder = ByteStringByteArrayBuilder(initialCapacity)

fun buildByteArray(block: ByteArrayBuilder.() -> Unit): ByteArray {
    val builder = ByteStringByteArrayBuilder()
    builder.block()
    return builder.toByteArray()
}

// TODO: replace with manual byte array, because ByteString.toByteArray() copies the array
class ByteStringByteArrayBuilder(initialCapacity: Int = 1024): ByteArrayBuilder {
    private val builder = ByteStringBuilder(initialCapacity) // 1 kb default size

    override fun append(byte: Byte, offset: Byte) {
        val sum = byte.toUByte() + offset.toUByte()
        require(sum <= UByte.MAX_VALUE) { "Overflow after adding offset $offset to byte $byte" }
        builder.append(sum.toByte())
    }

    override fun append(array: ByteArray) = builder.append(array)

    override fun toByteArray(): ByteArray = builder.toByteString().toByteArray()
}
