package io.github.vooft.kotlinsmile.adapter.decoder.structure

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.vooft.kotlinsmile.adapter.decoder.AbstractSmileDecoder
import io.github.vooft.kotlinsmile.adapter.decoder.common.keyString
import io.github.vooft.kotlinsmile.adapter.decoder.keyvalue.SmileValueDecoder
import io.github.vooft.kotlinsmile.decoder.SmileDecoderSession
import io.github.vooft.kotlinsmile.token.SmileKeyToken
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.modules.SerializersModule

@OptIn(ExperimentalSerializationApi::class)
class SmileObjectDecoderAdapter(
    private val session: SmileDecoderSession,
    override val serializersModule: SerializersModule
) : AbstractSmileDecoder(session), Decoder by SmileValueDecoder(session, serializersModule) {

    private val logger = KotlinLogging.logger { }

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        require(descriptor.kind == StructureKind.CLASS || descriptor.kind == StructureKind.OBJECT) {
            "Only class or object descriptor is supported"
        }

        val nextToken = session.peekKeyToken()
        logger.info { "Decoding object key $nextToken" }

        return when (nextToken) {
            SmileKeyToken.KeyEndObjectMarker -> {
                session.skip()
                CompositeDecoder.DECODE_DONE
            }

            else -> {
                val propertyName = session.keyString()
                descriptor.getElementIndex(propertyName)
            }
        }
    }

}