package io.github.vooft.kotlinsmile.encoder

import io.github.vooft.kotlinsmile.encoder.writers.HeaderWriter
import io.github.vooft.kotlinsmile.encoder.writers.HeaderWriterSession
import io.github.vooft.kotlinsmile.encoder.writers.SmallIntegerWriter
import io.github.vooft.kotlinsmile.encoder.writers.SmallIntegerWriterSession
import kotlinx.io.bytestring.ByteStringBuilder
import kotlinx.io.bytestring.buildByteString

class SmileEncoderFactory {
    fun write(block: SmileWriter.() -> Unit): ByteArray = buildByteString {
        SmileWriterSession(this).block()
    }.toByteArray()
}

interface SmileWriter : HeaderWriter, SmallIntegerWriter

class SmileWriterSession(byteStringBuilder: ByteStringBuilder) : SmileWriter,
    HeaderWriter by HeaderWriterSession(byteStringBuilder),
    SmallIntegerWriter by SmallIntegerWriterSession(byteStringBuilder)
