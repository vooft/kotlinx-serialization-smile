package io.github.vooft.kotlinsmile.encoder.writers

import io.github.vooft.kotlinsmile.smile.SimpleLiteral
import kotlinx.io.bytestring.ByteStringBuilder
import kotlin.experimental.or

interface SimpleLiteralWriter {
    fun emptyString()
    fun nullValue()
    fun boolean(value: Boolean)
}

class SimpleLiteralWriterSession(private val builder: ByteStringBuilder): SimpleLiteralWriter {
    override fun emptyString() {
        builder.append(SimpleLiteral.mask or LITERAL_EMPTY_STRING)
    }

    override fun nullValue() {
        builder.append(SimpleLiteral.mask or LITERAL_NULL)
    }

    override fun boolean(value: Boolean) {
        if (value) {
            builder.append(SimpleLiteral.mask or LITERAL_TRUE)
        } else {
            builder.append(SimpleLiteral.mask or LITERAL_FALSE)
        }
    }
}

private const val LITERAL_EMPTY_STRING: Byte = 0
private const val LITERAL_NULL: Byte = 1
private const val LITERAL_TRUE: Byte = 2
private const val LITERAL_FALSE: Byte = 3

