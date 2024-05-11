package io.github.vooft.kotlinsmile.adapter.encoder

import io.github.vooft.kotlinsmile.adapter.encoder.common.MutableEncoderDelegate
import io.github.vooft.kotlinsmile.adapter.encoder.keyvalue.SmileKeyEncoder
import io.github.vooft.kotlinsmile.adapter.encoder.keyvalue.SmileValueEncoder
import io.github.vooft.kotlinsmile.encoder.SmileEncoderSession
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule

class KeyValueSmileEncoder(
    session: SmileEncoderSession,
    serializersModule: SerializersModule,
    private val keyEncoder: SmileKeyEncoder = SmileKeyEncoder(session, serializersModule),
    private val valueEncoder: SmileValueEncoder = SmileValueEncoder(session, serializersModule),
    private val delegate: MutableEncoderDelegate = MutableEncoderDelegate(keyEncoder)
) : Encoder by delegate {
    fun setKeyMode() {
        delegate.delegate = keyEncoder
    }

    fun setValueMode() {
        delegate.delegate = valueEncoder
    }
}
