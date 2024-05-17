package io.github.vooft.kotlinsmile.encoder.keys

import io.github.vooft.kotlinsmile.common.SmileString

class DecidingSharedKeyStringWriter(
    private val delegate: KeyStringWriter,
    private val sharedKeyStringWriter: SharedKeyStringWriter
) : KeyStringWriter {

    override fun keyShortAscii(value: SmileString) = when {
        sharedKeyStringWriter.hasKey(value.value) -> sharedKeyStringWriter.keyShared(value.value)
        else -> delegate.keyShortAscii(value)
    }

    override fun keyShortUnicode(value: SmileString) = when {
        sharedKeyStringWriter.hasKey(value.value) -> sharedKeyStringWriter.keyShared(value.value)
        else -> delegate.keyShortUnicode(value)
    }

    override fun keyLongUnicode(value: SmileString) = when {
        sharedKeyStringWriter.hasKey(value.value) -> sharedKeyStringWriter.keyShared(value.value)
        else -> delegate.keyLongUnicode(value)
    }
}
