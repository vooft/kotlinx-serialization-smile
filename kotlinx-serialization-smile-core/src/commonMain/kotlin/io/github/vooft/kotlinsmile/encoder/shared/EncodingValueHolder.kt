package io.github.vooft.kotlinsmile.encoder.shared

interface EncodingValueHolder {
    fun store(value: String)
    fun lookup(value: String): Int?
}

class EncodingValueHolderImpl : EncodingValueHolder {

    private val valueToId = mutableMapOf<String, Int>()
    private var index = 0

    override fun store(value: String) {
        val existingIndex = valueToId[value]
        require(existingIndex == null) { "Value $value is already stored under index $existingIndex" }

        valueToId[value] = index++
    }

    override fun lookup(value: String): Int? {
        return valueToId[value]
    }
}

class DisabledEncodingValueHolder : EncodingValueHolder {
    override fun store(value: String) = Unit
    override fun lookup(value: String): Int? = null
}
