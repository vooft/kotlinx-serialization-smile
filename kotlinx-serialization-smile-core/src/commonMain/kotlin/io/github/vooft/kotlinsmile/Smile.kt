package io.github.vooft.kotlinsmile

import io.github.vooft.kotlinsmile.adapter.SmileDecoderAdapter
import io.github.vooft.kotlinsmile.adapter.SmileEncoderAdapter
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.serializer

/**
 * Entry point for Smile encoding/decoding.
 * The easiest way to start is to use companion object's methods that will use default configuration.
 */
open class Smile(private val config: SmileConfig) {
    fun <T> encode(serializer: SerializationStrategy<T>, value: T): ByteArray {
        val encoder = SmileEncoderAdapter(config)
        encoder.encodeSerializableValue(serializer, value)
        return encoder.toByteArray()
    }

    fun <T> decode(deserializer: DeserializationStrategy<T>, data: ByteArray): T {
        val decoder = SmileDecoderAdapter(data)
        return decoder.decodeSerializableValue(deserializer)
    }

    companion object : Smile(SmileConfig.DEFAULT)
}

inline fun <reified T> Smile.encode(value: T) = encode(serializer(), value)
inline fun <reified T> Smile.decode(data: ByteArray): T = decode(serializer(), data)

