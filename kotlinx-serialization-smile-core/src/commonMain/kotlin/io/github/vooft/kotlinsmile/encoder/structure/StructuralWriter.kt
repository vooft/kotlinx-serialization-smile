package io.github.vooft.kotlinsmile.encoder.structure

import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.github.vooft.kotlinsmile.token.SmileKeyToken.KeyEndObjectMarker
import io.github.vooft.kotlinsmile.token.SmileValueToken.EndArrayMarker
import io.github.vooft.kotlinsmile.token.SmileValueToken.StartArrayMarker
import io.github.vooft.kotlinsmile.token.SmileValueToken.StartObjectMarker

interface StructuralWriter {
    fun startObject()
    fun endObject()
    fun startArray()
    fun endArray()
}

class StructuralWriterSession(private val builder: ByteArrayBuilder): StructuralWriter {
    override fun startObject() = builder.append(StartObjectMarker.firstByte)

    override fun endObject() = builder.append(KeyEndObjectMarker.firstByte)

    override fun startArray() = builder.append(StartArrayMarker.firstByte)

    override fun endArray() = builder.append(EndArrayMarker.firstByte)
}
