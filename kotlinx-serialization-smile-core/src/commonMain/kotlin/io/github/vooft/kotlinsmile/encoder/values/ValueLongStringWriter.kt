package io.github.vooft.kotlinsmile.encoder.values

import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.github.vooft.kotlinsmile.common.SmileString
import io.github.vooft.kotlinsmile.common.requireAscii
import io.github.vooft.kotlinsmile.common.requireUnicode
import io.github.vooft.kotlinsmile.token.SmileMarkers
import io.github.vooft.kotlinsmile.token.SmileValueToken.LongAscii
import io.github.vooft.kotlinsmile.token.SmileValueToken.LongUnicode

interface ValueLongStringWriter {
    fun valueLongAscii(value: SmileString)
    fun valueLongUnicode(value: SmileString)
}

class ValueLongStringWriterSession(private val builder: ByteArrayBuilder) : ValueLongStringWriter {
    override fun valueLongAscii(value: SmileString) {
        value.requireAscii()

        builder.append(LongAscii.firstByte)
        builder.append(value.encoded)
        builder.append(SmileMarkers.STRING_END_MARKER)
    }

    override fun valueLongUnicode(value: SmileString) {
        value.requireUnicode()

        builder.append(LongUnicode.firstByte)
        builder.append(value.encoded)
        builder.append(SmileMarkers.STRING_END_MARKER)
    }
}

