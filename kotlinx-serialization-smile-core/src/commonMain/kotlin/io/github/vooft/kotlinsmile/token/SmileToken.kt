package io.github.vooft.kotlinsmile.token

sealed interface SmileToken {
    val tokenRange: IntRange

    companion object {
        val ALL_TOKENS = listOf(
            SmileValueToken.SmallInteger,
            SmileValueToken.SimpleLiteral,
            SmileValueToken.TinyUnicode,
            SmileValueToken.TinyAscii,
            SmileValueToken.ShortAscii,
            SmileValueToken.ShortUnicode,
            SmileValueToken.LongAscii,
            SmileValueToken.LongUnicode,
            SmileValueToken.StructuralMarker,
            SmileKeyToken.KeyShortAscii,
            SmileKeyToken.KeyLongUnicode,
            SmileKeyToken.KeyShortUnicode
        )

        fun fromByte(byte: Byte): SmileToken {
            return ALL_TOKENS.first { it.tokenRange.contains(byte.toInt() and 0xFF) }
        }
    }
}

sealed interface SmileValueToken : SmileToken {
    data object SmallInteger : SmileValueToken {
        override val tokenRange = 0xC0..0xDF

        val mask = tokenRange.first.toByte()
        val values = -16..15
    }

    data object SimpleLiteral : SmileValueToken {
        override val tokenRange = 0x20..0x23
        const val VALUE_EMPTY_STRING = 0x20.toByte()
        const val VALUE_NULL = 0x21.toByte()
        const val VALUE_TRUE = 0x22.toByte()
        const val VALUE_FALSE = 0x23.toByte()
    }

    sealed interface SmileValueShortStringToken : SmileValueToken {
        val mask: Byte get() = tokenRange.first.toByte()
        val lengths: IntRange
        val isUnicode: Boolean
    }

    data object TinyUnicode : SmileValueShortStringToken {
        override val tokenRange = 0x80..0x9F

        override val lengths = 2..33
        override val isUnicode = true
    }

    data object TinyAscii : SmileValueShortStringToken {
        override val tokenRange = 0x40..0x5F

        override val lengths = 1..32
        override val isUnicode = false
    }

    data object ShortAscii : SmileValueShortStringToken {
        override val tokenRange = 0x60..0x7F

        override val lengths = 33..64
        override val isUnicode = false
    }

    data object ShortUnicode : SmileValueShortStringToken {
        override val tokenRange = 0xA0..0xBF

        override val lengths = 34..65
        override val isUnicode = true
    }

    data object LongAscii : SmileValueToken {
        override val tokenRange = 0xE0..0xE0
        val firstByte = tokenRange.first.toByte()
    }

    data object LongUnicode : SmileValueToken {
        override val tokenRange = 0xE4..0xE4
        val firstByte = tokenRange.first.toByte()
    }

    data object StructuralMarker : SmileValueToken {
        override val tokenRange = 0xF8..0xFB

        const val START_ARRAY = 0xF8.toByte()
        const val END_ARRAY = 0xF9.toByte()
        const val START_OBJECT = 0xFA.toByte()
        const val END_OBJECT = 0xFB.toByte()
    }
}

sealed interface SmileKeyToken : SmileToken {
    data object KeyShortAscii : SmileKeyToken {
        override val tokenRange = 0x80..0xBF

        val mask = (tokenRange.first - 1).toByte()
        val BYTE_LENGTHS = 1..64
    }

    data object KeyLongUnicode : SmileKeyToken {
        override val tokenRange = 0x34..0x34

        val mask = tokenRange.first.toByte()
        val BYTE_LENGTHS = 65..1024
    }

    data object KeyShortUnicode : SmileKeyToken {
        override val tokenRange = 0xC0..0xF7

        val mask = (tokenRange.first - 2).toByte() // length starts with 2, so we subtract 1
        val BYTE_LENGTHS = 2..57
    }
}

object SmileMarkers {
    /**
     * Constant byte #0: 0x3A (ASCII ':')
     * Constant byte #1: 0x29 (ASCII ')')
     * Constant byte #2: 0x0A (ASCII linefeed, '\n')
     */
    val FIXED_HEADER = byteArrayOf(0x3A, 0x29, 0x0A)

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
    const val VERSION_MASK = 0x00.toByte()
    const val HAS_RAW_BINARY_MASK = 0x04.toByte()
    const val SHARED_STRING_VALUE_MASK = 0x02.toByte()
    const val SHARED_STRING_PROPERTY_NAME_MASK = 0x01.toByte()

    const val STRING_END_MARKER = 0xFC.toByte()
    const val EOF_MARKER = 0xFF.toByte()
}

