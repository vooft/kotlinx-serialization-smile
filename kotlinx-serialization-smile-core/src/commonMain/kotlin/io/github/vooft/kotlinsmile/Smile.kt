package io.github.vooft.kotlinsmile

import io.github.vooft.kotlinsmile.adapter.SmileDecoderAdapter
import io.github.vooft.kotlinsmile.adapter.SmileEncoderAdapter
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.serializer

open class Smile(private val config: SmileConfig) {
    fun <T> encode(serializer: SerializationStrategy<T>, value: T): ByteArray {
        val encoder = SmileEncoderAdapter(config)
        encoder.encodeSerializableValue(serializer, value)
        return encoder.toByteArray()
    }

    fun <T> encode(objWithSerializer: ObjWithSerializer<T>) = encode(objWithSerializer.serializer, objWithSerializer.obj)

    inline fun <reified T> encode(value: T) = encode(serializer(), value)

    fun <T> decode(deserializer: DeserializationStrategy<T>, data: ByteArray): T {
        val decoder = SmileDecoderAdapter(data)
        return decoder.decodeSerializableValue(deserializer)
    }

    inline fun <reified T> decode(data: ByteArray): T = decode(serializer(), data)

    companion object : Smile(SmileConfig.DEFAULT)
}

// temp solution for testing
class ObjWithSerializer<T>(val obj: T, val serializer: KSerializer<T>, val name: String?)
inline fun <reified T> ObjWithSerializer(obj: T, name: String? = null) = ObjWithSerializer(obj, serializer<T>(), name)

