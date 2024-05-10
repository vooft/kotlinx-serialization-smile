package io.github.vooft.kotlinsmile.token

sealed interface SmileToken {
    val tokenRange: IntRange
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
    const val STRING_END_MARKER = 0xFC.toByte()
    const val EOF_MARKER = 0xFF.toByte()
}



