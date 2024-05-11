package io.github.vooft.kotlinsmile.adapter.encoder.structure

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.vooft.kotlinsmile.adapter.encoder.common.AbstractSmileCompositeEncoder
import io.github.vooft.kotlinsmile.adapter.encoder.keyvalue.SmileValueEncoder
import io.github.vooft.kotlinsmile.encoder.SmileEncoderSession
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule

@OptIn(ExperimentalSerializationApi::class)
class SmileListEncoder(
    private val session: SmileEncoderSession,
    override val serializersModule: SerializersModule,
    valueEncoder: SmileValueEncoder = SmileValueEncoder(session, serializersModule)
) : AbstractSmileCompositeEncoder(valueEncoder), Encoder by valueEncoder {

    private val logger = KotlinLogging.logger { }

    override fun encodeElement(descriptor: SerialDescriptor, index: Int): Boolean {
        require(descriptor.kind == StructureKind.LIST) { "Can only encode lists, but found $descriptor" }
        logger.info { "Encoding element $index of an array" }
        return true
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        require(descriptor.kind == StructureKind.LIST) { "Can only encode lists, but found $descriptor" }
        logger.info { "Ending array" }
        session.endArray()
    }
}
