package io.github.vooft.kotlinsmile.smile

sealed interface SmileKeyToken {
    val range: IntRange
    val offset: Byte get() = (range.first - 1).toByte()
}

data object ShortAsciiName : SmileKeyToken {
    override val range = 0x80..0xBF
}


