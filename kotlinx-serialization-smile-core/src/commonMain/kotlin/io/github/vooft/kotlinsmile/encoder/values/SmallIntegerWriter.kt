package io.github.vooft.kotlinsmile.encoder.values

import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.github.vooft.kotlinsmile.common.ZigzagSmallInteger
import io.github.vooft.kotlinsmile.token.SmileValueToken.SmallInteger
import kotlin.jvm.JvmInline

interface SmallIntegerWriter {
    fun smallInteger(value: Byte)
    fun smallInteger(value: Int)
}

@JvmInline
value class SmallIntegerWriterSession(private val builder: ByteArrayBuilder) : SmallIntegerWriter {
    override fun smallInteger(value: Byte) {
        writeZigzagSmallInteger(ZigzagSmallInteger.fromPlain(value.toInt()))
    }

    override fun smallInteger(value: Int) {
        writeZigzagSmallInteger(ZigzagSmallInteger.fromPlain(value))
    }

    private fun writeZigzagSmallInteger(zigzag: ZigzagSmallInteger) {
        require(zigzag.plainValue in SmallInteger.VALUES_RANGE) {
            "Value must be in ${SmallInteger.VALUES_RANGE}, actual: ${zigzag.plainValue}"
        }

        builder.append(byte = zigzag.toEncoded(), orMask = SmallInteger.mask)
    }
}
