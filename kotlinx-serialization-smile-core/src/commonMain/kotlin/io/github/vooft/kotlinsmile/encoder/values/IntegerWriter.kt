package io.github.vooft.kotlinsmile.encoder.values

import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.github.vooft.kotlinsmile.common.ZigzagInteger
import io.github.vooft.kotlinsmile.encoder.values.AppendConfig.IntConfig
import io.github.vooft.kotlinsmile.encoder.values.AppendConfig.LongConfig
import io.github.vooft.kotlinsmile.token.SmileValueToken.LongInteger
import io.github.vooft.kotlinsmile.token.SmileValueToken.RegularInteger
import io.github.vooft.kotlinsmile.token.SmileValueToken.SmallInteger
import kotlin.jvm.JvmInline

interface IntegerWriter {
    fun valueSmallInteger(value: Int)
    fun valueRegularInteger(value: Int)
    fun valueLongInteger(value: Long)
}

@JvmInline
value class IntegerWriterSession(private val builder: ByteArrayBuilder) : IntegerWriter {
    override fun valueSmallInteger(value: Int) {
        require(value in SmallInteger.values) { "Value must be in ${SmallInteger.values}, actual: $value" }

        val zigzag = ZigzagInteger.encode(value)
        builder.append(byte = zigzag.toByte(), offset = SmallInteger.offset)
    }

    override fun valueRegularInteger(value: Int) {
        require(value in RegularInteger.values) { "Value must be in ${RegularInteger.values}, actual: $value" }

        builder.append(byte = RegularInteger.firstByte)
        builder.appendRawInt(ZigzagInteger.encode(value), IntConfig)
    }

    override fun valueLongInteger(value: Long) {
        require(value in LongInteger.values) { "Value must be in ${LongInteger.values}, actual: $value" }

        builder.append(byte = LongInteger.firstByte)
        builder.appendRawLong(ZigzagInteger.encode(value), LongConfig)
    }
}

