package io.github.vooft.kotlinsmile.smile

sealed interface SmileToken {
    val mask: Byte

    companion object {
        val ZERO_MASK: Byte = 0b000_1_1111u.toByte()
    }
}

data object SmallInteger: SmileToken {
    override val mask: Byte = 0b110_0_0000u.toByte()
}

data object SimpleLiteral : SmileToken {
    override val mask: Byte = 0b001_0_0000u.toByte()
}



