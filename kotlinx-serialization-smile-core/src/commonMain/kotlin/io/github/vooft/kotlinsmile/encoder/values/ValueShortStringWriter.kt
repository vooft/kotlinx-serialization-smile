package io.github.vooft.kotlinsmile.encoder.values

import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.github.vooft.kotlinsmile.common.SmileString
import io.github.vooft.kotlinsmile.common.length
import io.github.vooft.kotlinsmile.common.requireAscii
import io.github.vooft.kotlinsmile.common.requireLength
import io.github.vooft.kotlinsmile.common.requireUnicode
import io.github.vooft.kotlinsmile.common.shared.SmileSharedStorage
import io.github.vooft.kotlinsmile.token.SmileValueToken.ShortAscii
import io.github.vooft.kotlinsmile.token.SmileValueToken.ShortUnicode
import io.github.vooft.kotlinsmile.token.SmileValueToken.SmileValueShortStringToken
import io.github.vooft.kotlinsmile.token.SmileValueToken.TinyAscii
import io.github.vooft.kotlinsmile.token.SmileValueToken.TinyUnicode

interface ValueShortStringWriter {
    fun valueTinyAscii(value: SmileString)
    fun valueShortAscii(value: SmileString)
    fun valueTinyUnicode(value: SmileString)
    fun valueShortUnicode(value: SmileString)
}

class ValueShortStringWriterSession(
    private val builder: ByteArrayBuilder,
    private val sharedStorage: SmileSharedStorage
) : ValueShortStringWriter {

    override fun valueTinyAscii(value: SmileString) = TinyAscii.append(value)
    override fun valueShortAscii(value: SmileString) = ShortAscii.append(value)
    override fun valueTinyUnicode(value: SmileString) = TinyUnicode.append(value)
    override fun valueShortUnicode(value: SmileString) = ShortUnicode.append(value)

    private fun SmileValueShortStringToken.append(value: SmileString) {
        value.requireLength(lengths)
        if (isUnicode) {
            value.requireUnicode()
        } else {
            value.requireAscii()
        }

        sharedStorage.storeValue(value.value)

        val writtenLength = value.length - lengths.first
        builder.append(byte = writtenLength.toByte(), offset = offset)
        builder.append(value.encoded)
    }
}

class DecidingValueShortStringWriter(
    private val delegate: ValueShortStringWriter,
    private val sharedValueShortStringWriter: SharedValueShortStringWriter
) : ValueShortStringWriter {
    override fun valueTinyAscii(value: SmileString) = when (sharedValueShortStringWriter.hasValue(value.value)) {
        true -> sharedValueShortStringWriter.valueShared(value.value)
        false -> delegate.valueTinyAscii(value)
    }

    override fun valueShortAscii(value: SmileString) = when (sharedValueShortStringWriter.hasValue(value.value)) {
        true -> sharedValueShortStringWriter.valueShared(value.value)
        false -> delegate.valueShortAscii(value)
    }

    override fun valueTinyUnicode(value: SmileString) = when (sharedValueShortStringWriter.hasValue(value.value)) {
        true -> sharedValueShortStringWriter.valueShared(value.value)
        false -> delegate.valueTinyUnicode(value)
    }

    override fun valueShortUnicode(value: SmileString) = when (sharedValueShortStringWriter.hasValue(value.value)) {
        true -> sharedValueShortStringWriter.valueShared(value.value)
        false -> delegate.valueShortUnicode(value)
    }
}
