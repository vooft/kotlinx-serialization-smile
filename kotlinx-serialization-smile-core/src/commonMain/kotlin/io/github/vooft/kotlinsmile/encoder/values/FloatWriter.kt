package io.github.vooft.kotlinsmile.encoder.values

import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.github.vooft.kotlinsmile.encoder.values.raw.AppendConfig.DoubleConfig
import io.github.vooft.kotlinsmile.encoder.values.raw.AppendConfig.FloatConfig
import io.github.vooft.kotlinsmile.encoder.values.raw.appendRawInt
import io.github.vooft.kotlinsmile.encoder.values.raw.appendRawLong
import io.github.vooft.kotlinsmile.token.SmileValueToken.DoubleValue
import io.github.vooft.kotlinsmile.token.SmileValueToken.FloatValue

interface FloatWriter {
    fun valueFloat(value: Float)
    fun valueDouble(value: Double)
}

class FloatWriterSession(private val builder: ByteArrayBuilder) : FloatWriter {
    override fun valueFloat(value: Float) {
        builder.append(FloatValue.firstByte)
        builder.appendRawInt(value.toBits(), FloatConfig)
    }

    override fun valueDouble(value: Double) {
        builder.append(DoubleValue.firstByte)
        builder.appendRawLong(value.toBits(), DoubleConfig)
    }
}
