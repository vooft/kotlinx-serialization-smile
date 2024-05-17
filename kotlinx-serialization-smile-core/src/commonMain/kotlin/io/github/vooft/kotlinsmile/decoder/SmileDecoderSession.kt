package io.github.vooft.kotlinsmile.decoder

import io.github.vooft.kotlinsmile.adapter.decoder.common.peekKeyToken
import io.github.vooft.kotlinsmile.adapter.decoder.common.peekValueToken
import io.github.vooft.kotlinsmile.common.ByteArrayIterator
import io.github.vooft.kotlinsmile.common.shared.SmileSharedStorage
import io.github.vooft.kotlinsmile.decoder.keys.KeyStringReader
import io.github.vooft.kotlinsmile.decoder.keys.KeyStringReaderSession
import io.github.vooft.kotlinsmile.decoder.keys.SharedKeyStringReader
import io.github.vooft.kotlinsmile.decoder.keys.SharedKeyStringReaderSession
import io.github.vooft.kotlinsmile.decoder.structure.HeaderReader
import io.github.vooft.kotlinsmile.decoder.structure.HeaderReaderSession
import io.github.vooft.kotlinsmile.decoder.values.BinaryReader
import io.github.vooft.kotlinsmile.decoder.values.BinaryReaderSession
import io.github.vooft.kotlinsmile.decoder.values.FloatReader
import io.github.vooft.kotlinsmile.decoder.values.FloatReaderSession
import io.github.vooft.kotlinsmile.decoder.values.IntegerReader
import io.github.vooft.kotlinsmile.decoder.values.IntegerReaderSession
import io.github.vooft.kotlinsmile.decoder.values.SharedValueStringReader
import io.github.vooft.kotlinsmile.decoder.values.SharedValueStringReaderSession
import io.github.vooft.kotlinsmile.decoder.values.ValueLongStringReader
import io.github.vooft.kotlinsmile.decoder.values.ValueLongStringReaderSession
import io.github.vooft.kotlinsmile.decoder.values.ValueShortStringReader
import io.github.vooft.kotlinsmile.decoder.values.ValueShortStringReaderSession
import io.github.vooft.kotlinsmile.decoder.values.ValueSimpleLiteralReader
import io.github.vooft.kotlinsmile.decoder.values.ValueSimpleLiteralReaderSession
import io.github.vooft.kotlinsmile.token.SmileKeyToken
import io.github.vooft.kotlinsmile.token.SmileValueToken

class SmileDecoderSession(private val iterator: ByteArrayIterator, private val sharedStorage: SmileSharedStorage) :
    HeaderReader by HeaderReaderSession(iterator),
    KeyStringReader by KeyStringReaderSession(iterator, sharedStorage),
    IntegerReader by IntegerReaderSession(iterator),
    FloatReader by FloatReaderSession(iterator),
    ValueShortStringReader by ValueShortStringReaderSession(iterator, sharedStorage),
    ValueLongStringReader by ValueLongStringReaderSession(iterator),
    ValueSimpleLiteralReader by ValueSimpleLiteralReaderSession(iterator),
    BinaryReader by BinaryReaderSession(iterator),
    SharedKeyStringReader by SharedKeyStringReaderSession(iterator, sharedStorage),
    SharedValueStringReader by SharedValueStringReaderSession(iterator, sharedStorage) {

    fun skip() {
        iterator.next()
    }

    fun peekKeyToken(): SmileKeyToken = iterator.peekKeyToken()

    fun peekValueToken(): SmileValueToken = iterator.peekValueToken()
}

