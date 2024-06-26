package io.github.vooft.kotlinsmile.adapter

import io.github.vooft.kotlinsmile.adapter.decoder.keyvalue.SmileValueDecoder
import io.github.vooft.kotlinsmile.common.ByteArrayIteratorImpl
import io.github.vooft.kotlinsmile.common.shared.SmileSharedStorageImpl
import io.github.vooft.kotlinsmile.decoder.SmileDecoderSession
import io.github.vooft.kotlinsmile.decoder.structure.HeaderReaderSession
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

class SmileDecoderAdapter(
    private val session: SmileDecoderSession,
    override val serializersModule: SerializersModule = EmptySerializersModule()
) : Decoder by SmileValueDecoder(session, serializersModule) {

    constructor(data: ByteArray, serializersModule: SerializersModule = EmptySerializersModule()) : this(
        session = kotlin.run {
            val iterator = ByteArrayIteratorImpl(data)
            val header = HeaderReaderSession(iterator).header()
            SmileDecoderSession(
                iterator = iterator,
                sharedStorage = SmileSharedStorageImpl(shareKeys = header.shareKeys, shareValues = header.shareValues)
            )
        },
        serializersModule = serializersModule
    )
}
