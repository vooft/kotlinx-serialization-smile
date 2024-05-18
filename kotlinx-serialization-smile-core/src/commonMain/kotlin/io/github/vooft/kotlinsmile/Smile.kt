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
open class Smile(private val config: SmileConfig) : SmileEncoder, SmileDecoder {

    override fun <T> encode(serializer: SerializationStrategy<T>, value: T): ByteArray {
        val encoder = SmileEncoderAdapter(config)
        encoder.encodeSerializableValue(serializer, value)
        return encoder.toByteArray()
    }

    override fun <T> decode(deserializer: DeserializationStrategy<T>, data: ByteArray): T {
        val decoder = SmileDecoderAdapter(data)
        return decoder.decodeSerializableValue(deserializer)
    }

    companion object : SmileEncoder, SmileDecoder {
        override fun <T> encode(serializer: SerializationStrategy<T>, value: T): ByteArray {
            return Smile(SmileConfig.DEFAULT).encode(serializer, value)
        }

        override fun <T> decode(deserializer: DeserializationStrategy<T>, data: ByteArray): T {
            return Smile(SmileConfig.DEFAULT).decode(deserializer, data)
        }

    }
}

interface SmileEncoder {
    fun <T> encode(serializer: SerializationStrategy<T>, value: T): ByteArray
}

interface SmileDecoder {
    fun <T> decode(deserializer: DeserializationStrategy<T>, data: ByteArray): T
}

inline fun <reified T> SmileEncoder.encode(value: T) = encode(serializer(), value)
inline fun <reified T> SmileDecoder.decode(data: ByteArray): T = decode(serializer(), data)

