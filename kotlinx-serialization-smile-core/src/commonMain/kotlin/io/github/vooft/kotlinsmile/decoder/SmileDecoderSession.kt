package io.github.vooft.kotlinsmile.decoder

import io.github.vooft.kotlinsmile.common.ByteArrayIterator
import io.github.vooft.kotlinsmile.decoder.keys.KeyStringReader
import io.github.vooft.kotlinsmile.decoder.keys.KeyStringReaderSession
import io.github.vooft.kotlinsmile.decoder.structure.HeaderReader
import io.github.vooft.kotlinsmile.decoder.structure.HeaderReaderSession
import io.github.vooft.kotlinsmile.decoder.values.IntegerReader
import io.github.vooft.kotlinsmile.decoder.values.IntegerReaderSession
import io.github.vooft.kotlinsmile.decoder.values.ValueLongStringReader
import io.github.vooft.kotlinsmile.decoder.values.ValueLongStringReaderSession
import io.github.vooft.kotlinsmile.decoder.values.ValueShortStringReader
import io.github.vooft.kotlinsmile.decoder.values.ValueShortStringReaderSession
import io.github.vooft.kotlinsmile.decoder.values.ValueSimpleLiteralReader
import io.github.vooft.kotlinsmile.decoder.values.ValueSimpleLiteralReaderSession
import io.github.vooft.kotlinsmile.token.SmileKeyToken
import io.github.vooft.kotlinsmile.token.SmileToken
import io.github.vooft.kotlinsmile.token.SmileValueToken

class SmileDecoderSession(private val iterator: ByteArrayIterator) :
    HeaderReader by HeaderReaderSession(iterator),
    KeyStringReader by KeyStringReaderSession(iterator),
    IntegerReader by IntegerReaderSession(iterator),
    ValueShortStringReader by ValueShortStringReaderSession(iterator),
    ValueLongStringReader by ValueLongStringReaderSession(iterator),
    ValueSimpleLiteralReader by ValueSimpleLiteralReaderSession(iterator) {

    fun skip() {
        iterator.next()
    }

    fun peekKeyToken(): SmileKeyToken {
        val byte = iterator.next()
        iterator.rollback(1)
        return SmileToken.keyToken(byte) ?: error("Unknown key token: ${byte.toUByte().toString(16)}")
    }

    fun peekValueToken(): SmileValueToken {
        val byte = iterator.next()
        iterator.rollback(1)
        return SmileToken.valueToken(byte) ?: error("Unknown key token: ${byte.toUByte().toString(16)}")
    }
}

