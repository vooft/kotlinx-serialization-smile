package io.github.vooft.kotlinsmile.token

sealed interface SmileToken {
    val range: IntRange
}

sealed interface SmileValueToken : SmileToken {
    data object SmallInteger: SmileValueToken {
        override val range = 0xC0..0xDF

        val mask = range.first.toByte()
        val VALUES_RANGE = -16..15
    }

    data object SimpleLiteral : SmileValueToken {
        override val range = 0x20..0x23
        const val VALUE_EMPTY_STRING = 0x20.toByte()
        const val VALUE_NULL = 0x21.toByte()
        const val VALUE_TRUE = 0x22.toByte()
        const val VALUE_FALSE = 0x23.toByte()
    }

    data object TinyUnicode : SmileValueToken {
        override val range = 0x80..0x9F
        val mask = TinyAscii.range.first.toByte()
    }

    data object TinyAscii : SmileValueToken {
        override val range = 0x40..0x5F

        val mask = range.first.toByte()
        val LENGTH_RANGE = 1..32
    }

    data object StructuralMarker : SmileValueToken {
        override val range = 0xF8..0xFB

        const val START_ARRAY = 0xF8.toByte()
        const val END_ARRAY = 0xF9.toByte()
        const val START_OBJECT = 0xFA.toByte()
        const val END_OBJECT = 0xFB.toByte()
    }
}

sealed interface SmileKeyToken : SmileToken {
    data object KeyShortAscii : SmileKeyToken {
        override val range = 0x80..0xBF

        val mask = (range.first - 1).toByte()
        val BYTE_LENGTHS = 1..64
    }

    data object KeyLongAscii : SmileKeyToken {
        override val range = 0x34..0x34

        val mask = range.first.toByte()
        val BYTE_LENGTHS = 65..1024
    }

    data object KeyShortUnicode : SmileKeyToken {
        override val range = 0xC0..0xF7

        val mask = (range.first - 2).toByte() // length starts with 2, so we subtract 1
        val BYTE_LENGTHS = 2..57
    }
}

object SmileMarkers {
    const val STRING_END_MARKER = 0xFC.toByte()
    const val EOF_MARKER = 0xFF.toByte()
}



