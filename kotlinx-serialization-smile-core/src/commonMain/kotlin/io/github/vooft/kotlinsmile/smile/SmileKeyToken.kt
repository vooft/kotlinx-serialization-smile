package io.github.vooft.kotlinsmile.smile

sealed interface SmileKeyToken {
    val range: IntRange
    val offset: Byte
}

data object ShortAsciiName : SmileKeyToken {
    override val range = 0x80..0xBF
    override val offset = (0x80 - 1).toByte()
}


