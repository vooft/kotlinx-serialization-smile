package io.github.vooft.kotlinsmile.encoder

import kotlin.jvm.JvmInline

@JvmInline
value class ZigzagSmallInteger private constructor(private val intPlainValue: Int) {
    init {
        require(intPlainValue in -16..15) { "SmallInteger must be in range -16..15" }
    }

    val plainValue: Byte get() = intPlainValue.toByte()

    fun toEncoded() = encode(intPlainValue).toByte()
    fun toDecoded() = intPlainValue.toByte()

    companion object {
        fun fromPlain(plain: Int): ZigzagSmallInteger {
            return ZigzagSmallInteger(plain)
        }

        fun fromEncoded(encoded: Int): ZigzagSmallInteger {
            return ZigzagSmallInteger(decode(encoded))
        }
    }
}

private fun encode(value: Int): Int {
    return (value shl 1) xor (value shr 31)
}

private fun decode(value: Int): Int {
    return (value ushr 1) xor -(value and 1)
}
