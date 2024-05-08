package io.github.vooft.kotlinsmile.adapter

import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer

class SmileEncoderAdapter : AbstractEncoder() {
    val list = mutableListOf<Any>()

    override val serializersModule: SerializersModule = EmptySerializersModule()

    override fun encodeValue(value: Any) {
        list.add(value)
    }
}

fun <T> encodeToList(serializer: SerializationStrategy<T>, value: T): List<Any> {
    val encoder = SmileEncoderAdapter()
    encoder.encodeSerializableValue(serializer, value)
    return encoder.list
}

inline fun <reified T> encodeToList(value: T) = encodeToList(serializer(), value)

