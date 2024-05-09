package io.github.vooft.kotlinsmile.encoder

import io.github.vooft.kotlinsmile.encoder.writers.HeaderWriter
import io.github.vooft.kotlinsmile.encoder.writers.HeaderWriterSession
import io.github.vooft.kotlinsmile.encoder.writers.SmallIntegerWriter
import io.github.vooft.kotlinsmile.encoder.writers.SmallIntegerWriterSession
import io.github.vooft.kotlinsmile.encoder.writers.StructuralWriter
import io.github.vooft.kotlinsmile.encoder.writers.StructuralWriterSession
import io.github.vooft.kotlinsmile.encoder.writers.TinyAsciiWriter
import io.github.vooft.kotlinsmile.encoder.writers.TinyAsciiWriterSession
import io.github.vooft.kotlinsmile.encoder.writers.TinyUnicodeWriter
import io.github.vooft.kotlinsmile.encoder.writers.TinyUnicodeWriterSession
import kotlinx.io.bytestring.ByteStringBuilder
import kotlinx.io.bytestring.buildByteString

class SmileEncoderFactory {
    fun write(block: SmileWriter.() -> Unit): ByteArray = buildByteString {
        SmileWriterSession(this).block()
    }.toByteArray()
}

interface SmileWriter : HeaderWriter, SmallIntegerWriter, StructuralWriter, TinyAsciiWriter, TinyUnicodeWriter

class SmileWriterSession(byteStringBuilder: ByteStringBuilder) : SmileWriter,
    HeaderWriter by HeaderWriterSession(byteStringBuilder),
    SmallIntegerWriter by SmallIntegerWriterSession(byteStringBuilder),
    StructuralWriter by StructuralWriterSession(byteStringBuilder),
    TinyAsciiWriter by TinyAsciiWriterSession(byteStringBuilder),
    TinyUnicodeWriter by TinyUnicodeWriterSession(byteStringBuilder)
