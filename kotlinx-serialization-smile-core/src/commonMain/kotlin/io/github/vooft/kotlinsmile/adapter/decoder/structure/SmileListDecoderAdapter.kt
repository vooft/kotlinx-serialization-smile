package io.github.vooft.kotlinsmile.adapter.decoder.structure

import io.github.vooft.kotlinsmile.adapter.decoder.AbstractSmileDecoder
import io.github.vooft.kotlinsmile.adapter.decoder.keyvalue.SmileValueDecoder
import io.github.vooft.kotlinsmile.decoder.SmileDecoderSession
import io.github.vooft.kotlinsmile.token.SmileValueToken
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

@OptIn(ExperimentalSerializationApi::class)
class SmileListDecoderAdapter(
    private val session: SmileDecoderSession,
    override val serializersModule: SerializersModule = EmptySerializersModule(),
) : AbstractSmileDecoder(session), Decoder by SmileValueDecoder(session, serializersModule) {

    private var currentListElementIndex = 0

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        require(descriptor.kind == StructureKind.LIST) { "Only list deserialization is supported" }

        val nextValue = session.peekValueToken()
        if (nextValue == SmileValueToken.EndArrayMarker) {
            session.skip()
            return CompositeDecoder.DECODE_DONE
        } else {
            return currentListElementIndex++
        }
    }
}
