package io.github.vooft.kotlinsmile.encoder.values

import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.github.vooft.kotlinsmile.token.SmileValueToken.SimpleLiteralBoolean
import io.github.vooft.kotlinsmile.token.SmileValueToken.SimpleLiteralEmptyString
import io.github.vooft.kotlinsmile.token.SmileValueToken.SimpleLiteralNull

interface ValueSimpleLiteralWriter {
    fun emptyString()
    fun nullValue()
    fun boolean(value: Boolean)
}

class ValueSimpleLiteralWriterSession(private val builder: ByteArrayBuilder): ValueSimpleLiteralWriter {
    override fun emptyString() = builder.append(SimpleLiteralEmptyString.value)

    override fun nullValue() = builder.append(SimpleLiteralNull.value)

    override fun boolean(value: Boolean) = when (value) {
        true -> SimpleLiteralBoolean.valueTrue
        false -> SimpleLiteralBoolean.valueFalse
    }.let { builder.append(it) }
}


