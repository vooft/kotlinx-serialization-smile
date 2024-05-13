package io.github.vooft.kotlinsmile.encoder.values

import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.github.vooft.kotlinsmile.token.SmileValueToken.SimpleLiteralBoolean
import io.github.vooft.kotlinsmile.token.SmileValueToken.SimpleLiteralEmptyString
import io.github.vooft.kotlinsmile.token.SmileValueToken.SimpleLiteralNull

interface ValueSimpleLiteralWriter {
    fun valueEmptyString()
    fun valueNull()
    fun valueBoolean(value: Boolean)
}

class ValueSimpleLiteralWriterSession(private val builder: ByteArrayBuilder): ValueSimpleLiteralWriter {
    override fun valueEmptyString() = builder.append(SimpleLiteralEmptyString.value)

    override fun valueNull() = builder.append(SimpleLiteralNull.value)

    override fun valueBoolean(value: Boolean) = when (value) {
        true -> SimpleLiteralBoolean.valueTrue
        false -> SimpleLiteralBoolean.valueFalse
    }.let { builder.append(it) }
}


