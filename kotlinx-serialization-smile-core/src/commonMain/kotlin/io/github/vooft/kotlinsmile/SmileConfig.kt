package io.github.vooft.kotlinsmile

data class SmileConfig(
    /**
     * Write header with version and smile marker, if disabled can save 4 bytes and config will fall back to default
     */
    val writeHeader: Boolean = true,
    /**
     * Share string values, if enabled can save space for repeated strings values, disabled by default
     */
    val shareStringValue: Boolean,
    /**
     * Share property names, if enabled can save space for repeated property names, enabled by default
     */
    val sharePropertyName: Boolean,
    /**
     * Write value as 7-bit encoded, if disabled can save ~14% space, disabled by default
     */
    val writeValueAs7Bits: Boolean = true
) {

    init {
        if (!writeHeader) {
            require(sharePropertyName) { "Property names are shared by default, can not decode if write header is off" }
            require(!shareStringValue) { "String values are not shared by default, can not enable if write header is off" }
            require(writeValueAs7Bits) { "7-bit encoding is enabled by default, can not disable if write header is off" }
        }

        require(writeHeader) { "Skipping header is not supported yet" }
        require(writeValueAs7Bits) { "Writing binaries not as 7-bits is not supported yet" }
    }

    companion object {
        val DEFAULT = SmileConfig(shareStringValue = false, sharePropertyName = false)
    }
}
