package io.github.vooft.kotlinsmile

import io.github.vooft.kotlinsmile.adapter.SmileEncoderAdapter
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.serializer

object Smile {
    fun <T> encode(serializer: SerializationStrategy<T>, value: T): ByteArray {
        val encoder = SmileEncoderAdapter()
        encoder.encodeSerializableValue(serializer, value)
        return encoder.toByteArray()
    }

    fun <T> encode(objWithSerializer: ObjWithSerializer<T>) = encode(objWithSerializer.serializer, objWithSerializer.obj)

    inline fun <reified T> encode(value: T) = encode(serializer(), value)
}

// temp solution for testing
class ObjWithSerializer<T>(val obj: T, val serializer: SerializationStrategy<T>)
inline fun <reified T> ObjWithSerializer(obj: T) = ObjWithSerializer(obj, serializer<T>())

