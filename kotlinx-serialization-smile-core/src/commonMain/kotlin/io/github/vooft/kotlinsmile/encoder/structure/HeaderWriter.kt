package io.github.vooft.kotlinsmile.encoder.structure

import io.github.vooft.kotlinsmile.SmileConfig
import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.github.vooft.kotlinsmile.token.SmileMarkers.FIXED_HEADER
import io.github.vooft.kotlinsmile.token.SmileMarkers.HAS_RAW_BINARY_MASK
import io.github.vooft.kotlinsmile.token.SmileMarkers.SHARED_STRING_PROPERTY_NAME_MASK
import io.github.vooft.kotlinsmile.token.SmileMarkers.SHARED_STRING_VALUE_MASK
import io.github.vooft.kotlinsmile.token.SmileMarkers.VERSION_MASK
import kotlin.experimental.or
import kotlin.jvm.JvmInline

interface HeaderWriter {
    // TODO: handle raw binary flag properly
    fun header(config: SmileConfig, hasRawBinary: Boolean = false)
}

@JvmInline
value class HeaderWriterSession(private val builder: ByteArrayBuilder) : HeaderWriter {
    override fun header(config: SmileConfig, hasRawBinary: Boolean) = builder.run {
        append(FIXED_HEADER)

        val rawBinaryMask = HAS_RAW_BINARY_MASK.maskIf(hasRawBinary)
        val sharedPropertyNameMask = SHARED_STRING_PROPERTY_NAME_MASK.maskIf(config.sharePropertyName)
        val sharedStringValueMask = SHARED_STRING_VALUE_MASK.maskIf(config.shareStringValue)

        append(VERSION_MASK or rawBinaryMask or sharedPropertyNameMask or sharedStringValueMask)
    }
}



private fun SmileConfig.sharedStringValueMask(): Byte = when {
    shareStringValue -> SHARED_STRING_VALUE_MASK
    else -> 0
}

private fun SmileConfig.sharedPropertyNameMask(): Byte = when {
    sharePropertyName -> SHARED_STRING_PROPERTY_NAME_MASK
    else -> 0
}

fun Byte.maskIf(boolean: Boolean) = if (boolean) this else 0
