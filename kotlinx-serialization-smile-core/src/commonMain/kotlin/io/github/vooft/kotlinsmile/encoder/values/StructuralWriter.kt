package io.github.vooft.kotlinsmile.encoder.values

import io.github.vooft.kotlinsmile.smile.EndArray
import io.github.vooft.kotlinsmile.smile.EndObject
import io.github.vooft.kotlinsmile.smile.StartArray
import io.github.vooft.kotlinsmile.smile.StartObject
import kotlinx.io.bytestring.ByteStringBuilder
import kotlin.experimental.or

interface StructuralWriter {
    fun startObject()
    fun endObject()
    fun startArray()
    fun endArray()
}

class StructuralWriterSession(private val builder: ByteStringBuilder): StructuralWriter {
    override fun startObject() {
        builder.append(StartObject.mask or 0b11010)
    }

    override fun endObject() {
        builder.append(EndObject.mask or 0b11011)
    }

    override fun startArray() {
        builder.append(StartArray.mask or 0b11000)
    }

    override fun endArray() {
        builder.append(EndArray.mask or 0b11001)
    }
}
