package io.github.vooft.kotlinsmile.encoder.writers

import io.github.vooft.kotlinsmile.common.ZigzagSmallInteger
import io.github.vooft.kotlinsmile.smile.SmallInteger
import kotlinx.io.bytestring.ByteStringBuilder
import kotlin.experimental.or
import kotlin.jvm.JvmInline

interface SmallIntegerWriter {
    fun smallInteger(value: Byte)
    fun smallInteger(value: Int)
}

@JvmInline
value class SmallIntegerWriterSession(private val builder: ByteStringBuilder): SmallIntegerWriter {
    override fun smallInteger(value: Byte) = writeZigzagSmallInteger(ZigzagSmallInteger.fromPlain(value.toInt()))
    override fun smallInteger(value: Int) = writeZigzagSmallInteger(ZigzagSmallInteger.fromPlain(value))

    private fun writeZigzagSmallInteger(zigzag: ZigzagSmallInteger) = builder.append(SmallInteger.mask or zigzag.toEncoded())
}
