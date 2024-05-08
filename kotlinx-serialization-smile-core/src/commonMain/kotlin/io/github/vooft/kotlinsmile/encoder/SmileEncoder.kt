package io.github.vooft.kotlinsmile.encoder

import io.github.vooft.kotlinsmile.smile.SmallInteger
import kotlinx.io.bytestring.ByteStringBuilder
import kotlin.experimental.or

class SmileEncoder {
    fun ByteStringBuilder.writeHeader() {
        append(FIXED_HEADER)
        append(VARIABLE_BYTE)
    }

    fun ByteStringBuilder.writeSmallInteger(value: Int) = writeZigzagSmallInteger(ZigzagSmallInteger.fromPlain(value))

    fun ByteStringBuilder.writeSmallByte(value: Byte) = writeZigzagSmallInteger(ZigzagSmallInteger.fromPlain(value.toInt()))

    private fun ByteStringBuilder.writeZigzagSmallInteger(zigzag: ZigzagSmallInteger) {
        append(SmallInteger.mask or zigzag.toEncoded())
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
private val VARIABLE_BYTE = 0b0000_0_0_0_0u.toByte()
