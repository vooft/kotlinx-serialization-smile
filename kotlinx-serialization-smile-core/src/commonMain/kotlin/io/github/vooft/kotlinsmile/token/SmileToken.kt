package io.github.vooft.kotlinsmile.token

sealed interface SmileToken {
    val tokenRange: IntRange

    companion object {
        val VALUE_TOKENS: List<SmileValueToken> = listOf(
            SmileValueToken.SmallInteger,
            SmileValueToken.RegularInteger,
            SmileValueToken.LongInteger,
            SmileValueToken.FloatValue,
            SmileValueToken.DoubleValue,
            SmileValueToken.SimpleLiteralEmptyString,
            SmileValueToken.SimpleLiteralNull,
            SmileValueToken.SimpleLiteralBoolean,
            SmileValueToken.TinyUnicode,
            SmileValueToken.TinyAscii,
            SmileValueToken.ShortAscii,
            SmileValueToken.ShortUnicode,
            SmileValueToken.LongAscii,
            SmileValueToken.LongUnicode,
            SmileValueToken.BinaryValue,
            SmileValueToken.StartArrayMarker,
            SmileValueToken.EndArrayMarker,
            SmileValueToken.StartObjectMarker,
        )

        val KEY_TOKENS: List<SmileKeyToken> = listOf(
            SmileKeyToken.KeyShortAscii,
            SmileKeyToken.KeyLongUnicode,
            SmileKeyToken.KeyShortUnicode,
            SmileKeyToken.KeyEndObjectMarker,
        )

//        private val sortedValueTokens = VALUE_TOKENS.sortedBy { it.tokenRange.first }
//        private val sortedKeyTokens = KEY_TOKENS.sortedBy { it.tokenRange.first }

        private val VALUE_TOKENS_ARRAY = VALUE_TOKENS.buildArray()
        private val KEY_TOKENS_ARRAY = KEY_TOKENS.buildArray()

        fun valueToken(byte: Byte): SmileValueToken? {
            // TODO: improve performance?
//            return VALUE_TOKENS.firstOrNull { byte in it }
//            return sortedValueTokens.binarySearch(byte)
            return VALUE_TOKENS_ARRAY[byte.toInt() and 0xFF]
        }

        fun keyToken(byte: Byte): SmileKeyToken? {
            // TODO: improve performance?
//            return KEY_TOKENS.firstOrNull { byte in it }
//            return sortedKeyTokens.binarySearch(byte)
            return KEY_TOKENS_ARRAY[byte.toInt() and 0xFF]
        }

        private inline fun <reified T: SmileToken> List<T>.buildArray(): Array<T?> {
            val array = arrayOfNulls<T>(0xFF)
            for (t in this) {
                for (i in t.tokenRange) {
                    array[i] = t
                }
            }
            return array
        }

        private fun <T: SmileToken> List<T>.binarySearch(byte: Byte): T? {
            val b = byte.toInt() and 0xFF
            return binarySearch {
                val result = when (b) {
                    in it.tokenRange -> 0
                    else -> {
                        if (b < it.tokenRange.first) 1 else -1
                    }
                }
                result
            }.let { index ->
                if (index >= 0) this[index] else null
            }
        }
    }
}

sealed interface SmileValueToken : SmileToken {

    sealed interface SmileStringToken : SmileValueToken

    sealed interface SmileValueShortStringToken : SmileStringToken {
        val offset: Byte get() = tokenRange.first.toByte()
        val lengths: IntRange
        val isUnicode: Boolean
    }

    sealed interface SmileValueFirstByteToken : SmileValueToken {
        val firstByte: Byte get() = tokenRange.first.toByte()
    }

    data object SmallInteger : SmileValueToken {
        override val tokenRange = 0xC0..0xDF

        val offset = tokenRange.first.toByte()
        val values = -16..15
    }

    data object RegularInteger : SmileValueFirstByteToken {
        override val tokenRange = 0x24..0x24
        val values = Int.MIN_VALUE..Int.MAX_VALUE
    }

    data object LongInteger : SmileValueFirstByteToken {
        override val tokenRange = 0x25..0x25
        val values = Long.MIN_VALUE..Long.MAX_VALUE
    }

    data object FloatValue : SmileValueFirstByteToken {
        override val tokenRange = 0x28..0x28
        const val SERIALIZED_BYTES = 5
    }

    data object DoubleValue : SmileValueFirstByteToken {
        override val tokenRange = 0x29..0x29
        const val SERIALIZED_BYTES = 10
    }

    data object SimpleLiteralEmptyString : SmileStringToken {
        override val tokenRange = 0x20..0x20
        val value = tokenRange.first.toByte()
    }

    data object SimpleLiteralNull : SmileValueToken {
        override val tokenRange = 0x21..0x21
        val value = tokenRange.first.toByte()
    }

    data object SimpleLiteralBoolean : SmileValueToken {
        override val tokenRange = 0x22..0x23
        val valueFalse = tokenRange.first.toByte()
        val valueTrue = tokenRange.last.toByte()
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

    data object LongAscii : SmileStringToken {
        override val tokenRange = 0xE0..0xE0
        val firstByte = tokenRange.first.toByte()
    }

    data object LongUnicode : SmileStringToken, SmileValueFirstByteToken {
        override val tokenRange = 0xE4..0xE4
    }

    data object BinaryValue : SmileValueFirstByteToken {
        override val tokenRange = 0xE8..0xE8
    }

    data object StartArrayMarker : SmileValueFirstByteToken {
        override val tokenRange = 0xF8..0xF8
    }

    data object EndArrayMarker : SmileValueFirstByteToken {
        override val tokenRange = 0xF9..0xF9
    }

    // StartObject is a value, EndObject is a key
    data object StartObjectMarker : SmileValueFirstByteToken {
        override val tokenRange = 0xFA..0xFA
    }
}

sealed interface SmileKeyToken : SmileToken {

    sealed interface SmileKeyFirstByteToken : SmileKeyToken {
        val firstByte: Byte get() = tokenRange.first.toByte()
    }

    sealed interface SmileKeyStringToken : SmileKeyToken

    data object KeyShortAscii : SmileKeyStringToken {
        override val tokenRange = 0x80..0xBF

        val offset = (tokenRange.first - 1).toByte()
        val BYTE_LENGTHS = 1..64
    }

    data object KeyLongUnicode : SmileKeyStringToken {
        override val tokenRange = 0x34..0x34

        val firstByte = tokenRange.first.toByte()
        val BYTE_LENGTHS = 65..1024
    }

    data object KeyShortUnicode : SmileKeyStringToken {
        override val tokenRange = 0xC0..0xF7

        val offset = (tokenRange.first - 2).toByte() // length starts with 2, so we subtract 1
        val BYTE_LENGTHS = 2..57
    }

    // EndObject is more of a key marker, rather than value marker
    data object KeyEndObjectMarker : SmileKeyFirstByteToken {
        override val tokenRange = 0xFB..0xFB
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

operator fun SmileToken.contains(byte: Byte): Boolean = byte.toInt() and 0xFF in tokenRange

