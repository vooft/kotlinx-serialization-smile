package io.github.vooft.kotlinsmile.adapter

import io.github.vooft.kotlinsmile.adapter.decoder.keyvalue.SmileValueDecoder
import io.github.vooft.kotlinsmile.common.ByteArrayIteratorImpl
import io.github.vooft.kotlinsmile.decoder.SmileDecoderSession
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

class SmileDecoderAdapter(
    private val session: SmileDecoderSession,
    override val serializersModule: SerializersModule = EmptySerializersModule()
) : Decoder by SmileValueDecoder(session, serializersModule) {

    constructor(data: ByteArray, serializersModule: SerializersModule = EmptySerializersModule()): this(
        session = SmileDecoderSession(ByteArrayIteratorImpl(data)),
        serializersModule = serializersModule
    )

    @Suppress("UnusedPrivateProperty")
    private val config = session.header()
}
