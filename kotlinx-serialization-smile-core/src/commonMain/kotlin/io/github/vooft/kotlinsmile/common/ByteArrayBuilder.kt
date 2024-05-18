package io.github.vooft.kotlinsmile.common

import kotlin.math.max

interface ByteArrayBuilder {
    fun append(byte: Byte, offset: Byte = 0)
    fun append(array: ByteArray)

    fun isEmpty(): Boolean
    fun clear()

    fun toByteArray(): ByteArray
}

/**
 * Create non thread-safe [ByteArrayBuilder] with specified initial capacity (default 64 bytes)
 */
fun ByteArrayBuilder(initialCapacity: Int = 64): ByteArrayBuilder = SimpleByteArrayBuilder(initialCapacity)

private class SimpleByteArrayBuilder(initialCapacity: Int = 64) : ByteArrayBuilder {
    private var buffer = ByteArray(initialCapacity)
    private var index = 0

    override fun append(byte: Byte, offset: Byte) {
        val sum = byte.toUByte() + offset.toUByte()
        require(sum <= UByte.MAX_VALUE) { "Overflow after adding offset $offset to byte $byte" }

        ensureRoom(1)
        buffer[index++] = sum.toByte()
    }

    override fun append(array: ByteArray) {
        ensureRoom(array.size)

        array.copyInto(buffer, index)
        index += array.size
    }

    override fun isEmpty(): Boolean = index == 0

    override fun clear() {
        index = 0
    }

    override fun toByteArray(): ByteArray = buffer.copyOf(index)

    private fun ensureRoom(newDataSize: Int) {
        val requiredCapacity = index + newDataSize
        if (buffer.size >= requiredCapacity) {
            return
        }

        val desiredSize = max((buffer.size * 1.5).toInt(), requiredCapacity)
        val newBuffer = ByteArray(desiredSize)
        buffer.copyInto(newBuffer)
        buffer = newBuffer
    }
}
