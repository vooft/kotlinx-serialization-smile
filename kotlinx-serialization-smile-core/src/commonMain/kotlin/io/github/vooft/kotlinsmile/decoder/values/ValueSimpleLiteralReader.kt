package io.github.vooft.kotlinsmile.decoder.values

import io.github.vooft.kotlinsmile.common.ByteArrayIterator
import io.github.vooft.kotlinsmile.token.SmileValueToken.SimpleLiteralBoolean
import io.github.vooft.kotlinsmile.token.SmileValueToken.SimpleLiteralEmptyString
import io.github.vooft.kotlinsmile.token.SmileValueToken.SimpleLiteralNull

interface ValueSimpleLiteralReader {
    fun valueEmptyString(): String
    fun valueNull(): Any?
    fun valueBoolean(): Boolean
}

class ValueSimpleLiteralReaderSession(private val iterator: ByteArrayIterator): ValueSimpleLiteralReader {
    override fun valueEmptyString(): String {
        val byte = iterator.next()
        require(byte == SimpleLiteralEmptyString.value) { "Invalid token for empty string value: ${byte.toUByte().toString(16)}" }
        return ""
    }

    override fun valueNull(): Any? {
        val byte = iterator.next()
        require(byte == SimpleLiteralNull.value) { "Invalid token for null value: ${byte.toUByte().toString(16)}" }
        return null
    }

    override fun valueBoolean(): Boolean {
        return when (val byte = iterator.next()) {
            SimpleLiteralBoolean.valueFalse -> false
            SimpleLiteralBoolean.valueTrue -> true
            else -> throw IllegalArgumentException("Invalid token for boolean value: ${byte.toUByte().toString(16)}")
        }
    }
}
