package io.github.vooft.kotlinsmile.adapter.decoder

import io.github.vooft.kotlinsmile.decoder.SmileDecoderSession
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encoding.AbstractDecoder

// TODO: replace with basic CompositeDecoder implementation
@OptIn(ExperimentalSerializationApi::class)
abstract class AbstractSmileDecoder(private val session: SmileDecoderSession) : AbstractDecoder() {
    init {
        println(session)
    }
}
