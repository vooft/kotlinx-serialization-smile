package io.github.vooft.kotlinsmile.encoder.values

import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.github.vooft.kotlinsmile.smile.StructuralMarker

interface StructuralWriter {
    fun startObject()
    fun endObject()
    fun startArray()
    fun endArray()
}

class StructuralWriterSession(private val builder: ByteArrayBuilder): StructuralWriter {
    override fun startObject() = builder.append(StructuralMarker.START_OBJECT)

    override fun endObject() = builder.append(StructuralMarker.END_OBJECT)

    override fun startArray() = builder.append(StructuralMarker.START_ARRAY)

    override fun endArray() = builder.append(StructuralMarker.END_ARRAY)
}
