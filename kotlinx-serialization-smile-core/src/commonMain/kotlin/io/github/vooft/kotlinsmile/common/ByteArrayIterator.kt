package io.github.vooft.kotlinsmile.common

interface ByteArrayIterator {
    fun next(): Byte
    fun next(length: Int): ByteArray
    fun hasNext(): Boolean
    fun rollback(length: Int)
}

class ByteArrayIteratorImpl(private val array: ByteArray): ByteArrayIterator {
    private var index = 0

    override fun next(): Byte {
        require(hasNext()) { "No more elements in the iterator" }
        return array[index++]
    }

    override fun next(length: Int): ByteArray {
        require(hasNext()) { "No more elements in the iterator" }
        require(index + length <= array.size) { "Not enough elements in the iterator" }

        val result = array.copyOfRange(index, index + length)
        index += length
        return result
    }

    override fun hasNext(): Boolean {
        return index < array.size
    }

    override fun rollback(length: Int) {
        require(index >= length) { "Can't rollback more than read" }
        index -= length
    }
}
