package io.github.vooft.kotlinsmile.encoder.structure

import io.github.vooft.kotlinsmile.SmileConfig
import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.github.vooft.kotlinsmile.token.SmileMarkers.FIXED_HEADER
import io.github.vooft.kotlinsmile.token.SmileMarkers.HAS_RAW_BINARY_MASK
import io.github.vooft.kotlinsmile.token.SmileMarkers.SHARED_STRING_PROPERTY_NAME_MASK
import io.github.vooft.kotlinsmile.token.SmileMarkers.SHARED_STRING_VALUE_MASK
import io.github.vooft.kotlinsmile.token.SmileMarkers.VERSION_MASK
import kotlin.experimental.or

interface HeaderWriter {
    fun preallocateHeader()
    fun toByteArray(config: SmileConfig): ByteArray
}

class HeaderWriterSession(private val builder: ByteArrayBuilder) : HeaderWriter {

    private var preallocated = false

    override fun preallocateHeader() {
        require(builder.isEmpty()) { "Header should be preallocated only in an empty array" }

        preallocated = true

        builder.append(FIXED_HEADER)
        builder.append(VERSION_MASK)
    }

    override fun toByteArray(config: SmileConfig): ByteArray {
        require(preallocated) { "Header should be preallocated before anything" }

        val result = builder.toByteArray()

        val rawBinaryMask = HAS_RAW_BINARY_MASK.maskIf(!config.writeBinaryAs7Bits)
        val sharedPropertyNameMask = SHARED_STRING_PROPERTY_NAME_MASK.maskIf(config.shareKeys)
        val sharedStringValueMask = SHARED_STRING_VALUE_MASK.maskIf(config.shareValues)

        result[FLAG_BYTE_INDEX] = VERSION_MASK or rawBinaryMask or sharedPropertyNameMask or sharedStringValueMask

        return result
    }
}

private const val FLAG_BYTE_INDEX = 3


fun Byte.maskIf(boolean: Boolean) = if (boolean) this else 0
