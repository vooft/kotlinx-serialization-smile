package io.github.vooft.kotlinsmile.adapter.decoder.structure

import io.github.vooft.kotlinsmile.adapter.decoder.KeyValueSmileDecoder
import io.github.vooft.kotlinsmile.adapter.decoder.common.AbstractSmileCompositeDecoder
import io.github.vooft.kotlinsmile.decoder.SmileDecoderSession
import io.github.vooft.kotlinsmile.token.SmileKeyToken
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

@OptIn(ExperimentalSerializationApi::class)
class SmileMapDecoder(
    private val session: SmileDecoderSession,
    override val serializersModule: SerializersModule = EmptySerializersModule(),
    private val delegate: KeyValueSmileDecoder = KeyValueSmileDecoder(session, serializersModule)
) : AbstractSmileCompositeDecoder(delegate), Decoder by delegate {

    private var index = 0

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        require(descriptor.kind == StructureKind.MAP) { "Only map deserialization is supported" }

        if (index % 2 == 0) {
            delegate.setKeyMode()
            val token = session.peekKeyToken()
            if (token == SmileKeyToken.KeyEndObjectMarker) {
                session.skip()
                return CompositeDecoder.DECODE_DONE
            }

            return index++
        } else {
            delegate.setValueMode()
            return index++
        }
    }


}
