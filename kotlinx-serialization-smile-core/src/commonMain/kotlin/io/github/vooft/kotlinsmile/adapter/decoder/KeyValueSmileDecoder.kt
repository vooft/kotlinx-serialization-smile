package io.github.vooft.kotlinsmile.adapter.decoder

import io.github.vooft.kotlinsmile.adapter.decoder.common.MutableDecoderDelegate
import io.github.vooft.kotlinsmile.adapter.decoder.keyvalue.SmileKeyDecoder
import io.github.vooft.kotlinsmile.adapter.decoder.keyvalue.SmileValueDecoder
import io.github.vooft.kotlinsmile.decoder.SmileDecoderSession
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.modules.SerializersModule

class KeyValueSmileDecoder(
    session: SmileDecoderSession,
    serializersModule: SerializersModule,
    private val keyDecoder: SmileKeyDecoder = SmileKeyDecoder(session, serializersModule),
    private val valueDecoder: SmileValueDecoder = SmileValueDecoder(session, serializersModule),
    private val delegate: MutableDecoderDelegate = MutableDecoderDelegate(keyDecoder)
) : Decoder by delegate {
    fun setKeyMode() {
        delegate.delegate = keyDecoder
    }

    fun setValueMode() {
        delegate.delegate = valueDecoder
    }
}
