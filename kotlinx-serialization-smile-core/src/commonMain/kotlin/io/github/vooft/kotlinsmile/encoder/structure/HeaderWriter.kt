package io.github.vooft.kotlinsmile.encoder.structure

import io.github.vooft.kotlinsmile.SmileConfig
import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
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

/**
 * Constant byte #0: 0x3A (ASCII ':')
 * Constant byte #1: 0x29 (ASCII ')')
 * Constant byte #2: 0x0A (ASCII linefeed, '\n')
 */
private val FIXED_HEADER = byteArrayOf(0x3A, 0x29, 0x0A)

/**
 * Bits 4-7 (4 MSB): 4-bit version number; 0x00 for current version (note: it is possible that some bits may be reused if necessary)
 *
 * Bits 3: Reserved
 *
 * Bit 2 (mask 0x04) Whether '''raw binary''' (unescaped 8-bit) values may be present in content
 *
 * Bit 1 (mask 0x02): Whether '''shared String value''' checking was enabled during encoding -- if header missing,
 * default value of "false" must be assumed for decoding (meaning parser need not store decoded String values for back referencing)
 *
 * Bit 0 (mask 0x01): Whether '''shared property name''' checking was enabled during encoding -- if header missing,
 * default value of "true" must be assumed for decoding (meaning parser MUST store seen property names for possible back references)
 */
private val VERSION_MASK = 0x00.toByte()
private val HAS_RAW_BINARY_MASK = 0x04.toByte()
private val SHARED_STRING_VALUE_MASK = 0x02.toByte()
private val SHARED_STRING_PROPERTY_NAME_MASK = 0x01.toByte()

private fun SmileConfig.sharedStringValueMask(): Byte = when {
    shareStringValue -> SHARED_STRING_VALUE_MASK
    else -> 0
}

private fun SmileConfig.sharedPropertyNameMask(): Byte = when {
    sharePropertyName -> SHARED_STRING_PROPERTY_NAME_MASK
    else -> 0
}

fun Byte.maskIf(boolean: Boolean) = if (boolean) this else 0
