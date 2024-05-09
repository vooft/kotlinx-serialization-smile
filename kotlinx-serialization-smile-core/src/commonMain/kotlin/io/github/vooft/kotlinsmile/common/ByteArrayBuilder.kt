package io.github.vooft.kotlinsmile.common

import kotlinx.io.bytestring.ByteStringBuilder

interface ByteArrayBuilder {
    fun append(byte: Byte, orMask: Byte = 0)
    fun append(array: ByteArray)

    fun toByteArray(): ByteArray
}

fun ByteArrayBuilder(): ByteArrayBuilder = ByteStringByteArrayBuilder()

fun buildByteArray(block: ByteArrayBuilder.() -> Unit): ByteArray {
    val builder = ByteStringByteArrayBuilder()
    builder.block()
    return builder.toByteArray()
}

class ByteStringByteArrayBuilder: ByteArrayBuilder {
    private val builder = ByteStringBuilder()

    override fun append(byte: Byte, orMask: Byte) {
        val sum = byte.toUByte() + orMask.toUByte()
        require(sum <= UByte.MAX_VALUE) { "Overflow after adding offset $orMask to byte $byte" }
        builder.append(sum.toByte())
    }

    override fun append(array: ByteArray) = builder.append(array)

    override fun toByteArray(): ByteArray = builder.toByteString().toByteArray()
}
