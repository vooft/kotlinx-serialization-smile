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
        builder.append(SimpleLiteral.mask or 0)
    }

    override fun nullValue() {
        builder.append(SimpleLiteral.mask or 1)
    }

    override fun boolean(value: Boolean) {
        if (value) {
            builder.append(SimpleLiteral.mask or 2)
        } else {
            builder.append(SimpleLiteral.mask or 3)
        }
    }
}


