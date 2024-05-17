package io.github.vooft.kotlinsmile

data class SmileConfig(
    val writeHeader: Boolean = true,
    val shareStringValue: Boolean,
    val sharePropertyName: Boolean,
    val writeValueAs7Bits: Boolean = true
) {

    init {
        if (!writeHeader) {
            require(sharePropertyName) { "Property names are shared by default, can not decode if write header is off" }
            require(!shareStringValue) { "String values are not shared by default, can not enable if write header is off" }
            require(writeValueAs7Bits) { "7-bit encoding is enabled by default, can not disable if write header is off" }
        }
    }

    companion object {
        val DEFAULT = SmileConfig(shareStringValue = false, sharePropertyName = false)
    }
}
