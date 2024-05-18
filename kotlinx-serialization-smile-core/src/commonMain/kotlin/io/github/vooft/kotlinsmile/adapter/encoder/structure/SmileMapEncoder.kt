package io.github.vooft.kotlinsmile.adapter.encoder.structure

import io.github.vooft.kotlinsmile.adapter.encoder.KeyValueSmileEncoder
import io.github.vooft.kotlinsmile.adapter.encoder.common.AbstractSmileCompositeEncoder
import io.github.vooft.kotlinsmile.encoder.SmileEncoderSession
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule

@OptIn(ExperimentalSerializationApi::class)
class SmileMapEncoder(
    private val session: SmileEncoderSession,
    override val serializersModule: SerializersModule,
    private val delegate: KeyValueSmileEncoder = KeyValueSmileEncoder(session, serializersModule)
) : AbstractSmileCompositeEncoder(delegate), Encoder by delegate {

    override fun encodeElement(descriptor: SerialDescriptor, index: Int): Boolean {
        require(descriptor.kind == StructureKind.MAP) { "Can only encode lists, but found $descriptor" }

        if (index % 2 == 0) {
            delegate.setKeyMode()
        } else {
            delegate.setValueMode()
        }

        return true
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        require(descriptor.kind == StructureKind.MAP) { "Can only encode lists, but found $descriptor" }

        session.endObject()
    }
}
