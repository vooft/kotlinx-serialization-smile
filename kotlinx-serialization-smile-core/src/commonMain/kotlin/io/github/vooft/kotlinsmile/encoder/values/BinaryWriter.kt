package io.github.vooft.kotlinsmile.encoder.values

import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.github.vooft.kotlinsmile.encoder.values.raw.AppendConfig.IntConfig
import io.github.vooft.kotlinsmile.encoder.values.raw.appendRawBinary
import io.github.vooft.kotlinsmile.encoder.values.raw.appendRawInt
import io.github.vooft.kotlinsmile.token.SmileValueToken.BinaryValue

interface BinaryWriter {
    fun valueBinary(data: ByteArray)
}

class BinaryWriterSession(private val builder: ByteArrayBuilder) : BinaryWriter {
    override fun valueBinary(data: ByteArray) {
        builder.append(BinaryValue.firstByte)
        builder.appendRawInt(data.size, IntConfig)
        builder.appendRawBinary(data)
    }
}


