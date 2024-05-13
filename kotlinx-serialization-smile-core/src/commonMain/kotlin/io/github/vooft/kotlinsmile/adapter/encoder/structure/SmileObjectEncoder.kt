package io.github.vooft.kotlinsmile.adapter.encoder.structure

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.vooft.kotlinsmile.adapter.encoder.common.AbstractSmileCompositeEncoder
import io.github.vooft.kotlinsmile.adapter.encoder.common.keyString
import io.github.vooft.kotlinsmile.adapter.encoder.keyvalue.SmileValueEncoder
import io.github.vooft.kotlinsmile.common.toSmile
import io.github.vooft.kotlinsmile.encoder.SmileEncoderSession
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule

@OptIn(ExperimentalSerializationApi::class)
class SmileObjectEncoder(
    private val session: SmileEncoderSession,
    override val serializersModule: SerializersModule,
    valueEncoder: SmileValueEncoder = SmileValueEncoder(session, serializersModule)
) : AbstractSmileCompositeEncoder(valueEncoder), Encoder by valueEncoder {

    private val logger = KotlinLogging.logger { }

    override fun encodeElement(descriptor: SerialDescriptor, index: Int): Boolean {
        require(descriptor.kind is StructureKind.CLASS || descriptor.kind is StructureKind.OBJECT) {
            "Can only encode classes or objects, but found $descriptor"
        }

        val name = descriptor.getElementName(index).toSmile()
        logger.debug { "encodeElement: descriptor=${descriptor.kind}, property name=$name" }

        session.keyString(name)

        return true
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        require(descriptor.kind == StructureKind.CLASS || descriptor.kind == StructureKind.OBJECT) {
            "Can only encode classes or objects, but found $descriptor"
        }

        logger.debug { "Ending object" }

        session.endObject()
    }
}
