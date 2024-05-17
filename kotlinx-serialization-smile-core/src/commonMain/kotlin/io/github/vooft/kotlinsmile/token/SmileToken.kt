package io.github.vooft.kotlinsmile.token

sealed class SmileToken(val tokenRange: IntRange) {

    private val cachedHashCode = tokenRange.hashCode()

    final override fun equals(other: Any?): Boolean {
        return other === this
    }

    final override fun hashCode() = cachedHashCode
}

object SmileTokensHolder {
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
        SmileValueToken.ShortSharedValue,
        SmileValueToken.LongSharedValue,
    )

    val KEY_TOKENS: List<SmileKeyToken> = listOf(
        SmileKeyToken.KeyShortAscii,
        SmileKeyToken.KeyLongUnicode,
        SmileKeyToken.KeyShortUnicode,
        SmileKeyToken.ShortSharedKey,
        SmileKeyToken.LongSharedKey,
        SmileKeyToken.KeyEndObjectMarker,
    )

    private val VALUE_TOKENS_ARRAY = VALUE_TOKENS.buildArray()
    private val KEY_TOKENS_ARRAY = KEY_TOKENS.buildArray()

    fun valueToken(byte: Byte): SmileValueToken? {
        return VALUE_TOKENS_ARRAY[byte.toInt() and 0xFF]
    }

    fun keyToken(byte: Byte): SmileKeyToken? {
        return KEY_TOKENS_ARRAY[byte.toInt() and 0xFF]
    }

    private inline fun <reified T : SmileToken> List<T>.buildArray(): Array<T?> {
        val array = arrayOfNulls<T>(0xFF)
        for (token in this) {
            for (i in token.tokenRange) {
                array[i] = token
            }
        }
        return array
    }
}

sealed class SmileValueToken(tokenRange: IntRange) : SmileToken(tokenRange) {

    sealed interface SmileStringToken

    sealed class SmileValueShortStringToken(tokenRange: IntRange, val lengths: IntRange, val isUnicode: Boolean) : SmileStringToken,
        SmileValueToken(tokenRange) {
        val offset: Byte get() = tokenRange.first.toByte()
    }

    sealed class SmileValueFirstByteToken(tokenRange: IntRange) : SmileValueToken(tokenRange) {
        val firstByte: Byte get() = tokenRange.first.toByte()
    }

    object SmallInteger : SmileValueToken(0xC0..0xDF) {
        val offset = tokenRange.first.toByte()
        val values = -16..15
    }

    object RegularInteger : SmileValueFirstByteToken(0x24..0x24) {
        val values = Int.MIN_VALUE..Int.MAX_VALUE
    }

    object LongInteger : SmileValueFirstByteToken(0x25..0x25) {
        val values = Long.MIN_VALUE..Long.MAX_VALUE
    }

    object FloatValue : SmileValueFirstByteToken(0x28..0x28) {
        const val SERIALIZED_BYTES = 5
    }

    object DoubleValue : SmileValueFirstByteToken(0x29..0x29) {
        const val SERIALIZED_BYTES = 10
    }

    object SimpleLiteralEmptyString : SmileStringToken, SmileValueToken(0x20..0x20) {
        val value = tokenRange.first.toByte()
    }

    object SimpleLiteralNull : SmileValueToken(0x21..0x21) {
        val value = tokenRange.first.toByte()
    }

    object SimpleLiteralBoolean : SmileValueToken(0x22..0x23) {
        val valueFalse = tokenRange.first.toByte()
        val valueTrue = tokenRange.last.toByte()
    }

    object TinyUnicode : SmileValueShortStringToken(tokenRange = 0x80..0x9F, lengths = 2..33, isUnicode = true)

    object TinyAscii : SmileValueShortStringToken(tokenRange = 0x40..0x5F, lengths = 1..32, isUnicode = false)

    object ShortAscii : SmileValueShortStringToken(tokenRange = 0x60..0x7F, lengths = 33..64, isUnicode = false)

    object ShortUnicode : SmileValueShortStringToken(tokenRange = 0xA0..0xBF, lengths = 34..65, isUnicode = true)

    object LongAscii : SmileStringToken, SmileValueToken(0xE0..0xE0) {
        val firstByte = tokenRange.first.toByte()
    }

    object LongUnicode : SmileStringToken, SmileValueFirstByteToken(0xE4..0xE4)

    object BinaryValue : SmileValueFirstByteToken(0xE8..0xE8)

    object StartArrayMarker : SmileValueFirstByteToken(0xF8..0xF8)

    object EndArrayMarker : SmileValueFirstByteToken(0xF9..0xF9)

    // StartObject is a value, EndObject is a key
    object StartObjectMarker : SmileValueFirstByteToken(0xFA..0xFA)

    object ShortSharedValue : SmileValueToken(0x01..0x1F) {
        val offset = tokenRange.first
    }

    object LongSharedValue : SmileValueToken(0xEC..0xEF) {
        val firstByte = tokenRange.first.toByte()
        val VALUES_RANGE = 31..1024
    }
}

sealed class SmileKeyToken(tokenRange: IntRange) : SmileToken(tokenRange) {

    sealed class SmileKeyFirstByteToken(tokenRange: IntRange) : SmileKeyToken(tokenRange) {
        val firstByte: Byte get() = tokenRange.first.toByte()
    }

    sealed interface SmileKeyStringToken

    object KeyShortAscii : SmileKeyStringToken, SmileKeyToken(0x80..0xBF) {
        val offset = (tokenRange.first - 1).toByte()
        val BYTE_LENGTHS = 1..64
    }

    object KeyLongUnicode : SmileKeyStringToken, SmileKeyToken(0x34..0x34) {
        val firstByte = tokenRange.first.toByte()
        val BYTE_LENGTHS = 65..1024
    }

    object KeyShortUnicode : SmileKeyStringToken, SmileKeyToken(0xC0..0xF7) {
        val offset = (tokenRange.first - 2).toByte() // length starts with 2, so we subtract 1
        val BYTE_LENGTHS = 2..57
    }

    object ShortSharedKey : SmileKeyStringToken, SmileKeyToken(0x40..0x7F) {
        val offset = tokenRange.first.toByte()
    }

    object LongSharedKey : SmileKeyStringToken, SmileKeyToken(0x30..0x33) {
        val firstByte = tokenRange.first.toByte()
        val VALUES_RANGE = 64..<1024
    }

    // EndObject is more of a key marker, rather than value marker
    object KeyEndObjectMarker : SmileKeyFirstByteToken(0xFB..0xFB)
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

