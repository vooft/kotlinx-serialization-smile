package io.github.vooft.kotlinsmile.encoder.values

import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.github.vooft.kotlinsmile.token.SmileValueToken.SimpleLiteral

interface ValueSimpleLiteralWriter {
    fun emptyString()
    fun nullValue()
    fun boolean(value: Boolean)
}

class ValueSimpleLiteralWriterSession(private val builder: ByteArrayBuilder): ValueSimpleLiteralWriter {
    override fun emptyString() = builder.append(SimpleLiteral.VALUE_EMPTY_STRING)

    override fun nullValue() = builder.append(SimpleLiteral.VALUE_NULL)

    override fun boolean(value: Boolean) = when (value) {
        true -> SimpleLiteral.VALUE_TRUE
        false -> SimpleLiteral.VALUE_FALSE
    }.let { builder.append(it) }
}


