package io.github.vooft.kotlinsmile

data class SmileConfig(
    /**
     * Write header with version and smile marker, if disabled can save 4 bytes and config will fall back to default
     */
    val writeHeader: Boolean = true,
    /**
     * Share string values, if enabled can save space for repeated strings values, disabled by default
     */
    val shareValues: Boolean,
    /**
     * Share property names, if enabled can save space for repeated property names, enabled by default
     */
    val shareKeys: Boolean,
    /**
     * Write binary values as 7-bit encoded, if disabled can save ~14% space, disabled by default
     */
    val writeBinaryAs7Bits: Boolean = true
) {

    init {
        if (!writeHeader) {
            require(shareKeys) { "Property names are shared by default, can not decode if write header is off" }
            require(!shareValues) { "String values are not shared by default, can not enable if write header is off" }
            require(writeBinaryAs7Bits) { "7-bit encoding is enabled by default, can not disable if write header is off" }
        }

        require(writeHeader) { "Skipping header is not supported yet" }
        require(writeBinaryAs7Bits) { "Writing binaries not as 7-bits is not supported yet" }
    }

    companion object {
        val DEFAULT = SmileConfig(shareValues = false, shareKeys = false)
    }
}
