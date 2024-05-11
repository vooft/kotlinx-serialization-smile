package io.github.vooft.kotlinsmile.decoder

import io.github.vooft.kotlinsmile.common.ByteArrayIterator
import io.github.vooft.kotlinsmile.decoder.keys.KeyStringReader
import io.github.vooft.kotlinsmile.decoder.keys.KeyStringReaderSession
import io.github.vooft.kotlinsmile.decoder.structure.HeaderReader
import io.github.vooft.kotlinsmile.decoder.structure.HeaderReaderSession
import io.github.vooft.kotlinsmile.decoder.values.SmallIntegerReader
import io.github.vooft.kotlinsmile.decoder.values.SmallIntegerReaderSession
import io.github.vooft.kotlinsmile.decoder.values.ValueShortStringReader
import io.github.vooft.kotlinsmile.decoder.values.ValueShortStringReaderSession
import io.github.vooft.kotlinsmile.token.SmileKeyToken
import io.github.vooft.kotlinsmile.token.SmileToken
import io.github.vooft.kotlinsmile.token.SmileValueToken

class SmileDecoderSession(private val iterator: ByteArrayIterator) :
    HeaderReader by HeaderReaderSession(iterator),
    KeyStringReader by KeyStringReaderSession(iterator),
    SmallIntegerReader by SmallIntegerReaderSession(iterator),
    ValueShortStringReader by ValueShortStringReaderSession(iterator) {

    fun skip() {
        iterator.next()
    }

    fun peekKeyToken(): SmileKeyToken {
        val byte = iterator.next()
        iterator.rollback(1)
        return SmileToken.keyToken(byte)
    }

    fun peekValueToken(): SmileValueToken {
        val byte = iterator.next()
        iterator.rollback(1)
        return SmileToken.valueToken(byte)
    }
}

