package io.github.vooft.kotlinsmile.common

interface ByteArrayIterator {
    fun next(): Byte
    fun nextByteArray(length: Int): ByteArray
    fun nextString(length: Int): String
    fun hasNext(): Boolean
    fun rollback(length: Int)
}

class ByteArrayIteratorImpl(private val array: ByteArray): ByteArrayIterator {
    private var index = 0

    override fun next(): Byte {
        require(hasNext()) { "No more elements in the iterator" }
        return array[index++]
    }

    override fun nextByteArray(length: Int): ByteArray {
        require(index + length <= array.size) { "Not enough elements in the iterator" }

        val result = array.copyOfRange(index, index + length)
        index += length
        return result
    }

    override fun nextString(length: Int): String {
        require(index + length <= array.size) { "Not enough elements in the iterator" }
        return array.decodeToString(index, index + length).also { index += length }
    }

    override fun hasNext(): Boolean {
        return index < array.size
    }

    override fun rollback(length: Int) {
        require(index >= length) { "Can't rollback more than read" }
        index -= length
    }
}
