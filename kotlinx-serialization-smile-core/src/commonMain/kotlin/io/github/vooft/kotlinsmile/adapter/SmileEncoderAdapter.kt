package io.github.vooft.kotlinsmile.adapter

import io.github.vooft.kotlinsmile.SmileConfig
import io.github.vooft.kotlinsmile.adapter.encoder.keyvalue.SmileValueEncoder
import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.github.vooft.kotlinsmile.encoder.SmileEncoderSession
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

class SmileEncoderAdapter(
    private val config: SmileConfig,
    private val session: SmileEncoderSession = SmileEncoderSession(ByteArrayBuilder(10240)),
    override val serializersModule: SerializersModule = EmptySerializersModule()
) : Encoder by SmileValueEncoder(session, serializersModule) {

    init {
        session.header(config)
    }

    fun toByteArray(): ByteArray = session.toByteArray()
}

