package io.github.vooft.kotlinsmile.encoder

import io.github.vooft.kotlinsmile.encoder.keys.KeyShortAsciiWriter
import io.github.vooft.kotlinsmile.encoder.keys.KeyShortAsciiWriterSession
import io.github.vooft.kotlinsmile.encoder.values.HeaderWriter
import io.github.vooft.kotlinsmile.encoder.values.HeaderWriterSession
import io.github.vooft.kotlinsmile.encoder.values.SmallIntegerWriter
import io.github.vooft.kotlinsmile.encoder.values.SmallIntegerWriterSession
import io.github.vooft.kotlinsmile.encoder.values.StructuralWriter
import io.github.vooft.kotlinsmile.encoder.values.StructuralWriterSession
import io.github.vooft.kotlinsmile.encoder.values.TinyAsciiWriter
import io.github.vooft.kotlinsmile.encoder.values.TinyAsciiWriterSession
import io.github.vooft.kotlinsmile.encoder.values.TinyUnicodeWriter
import io.github.vooft.kotlinsmile.encoder.values.TinyUnicodeWriterSession
import kotlinx.io.bytestring.ByteStringBuilder
import kotlinx.io.bytestring.buildByteString

class SmileEncoderFactory {
    fun write(block: SmileWriter.() -> Unit): ByteArray = buildByteString {
        SmileWriterSession(this).block()
    }.toByteArray()
}

interface SmileWriter : HeaderWriter, SmallIntegerWriter, StructuralWriter, TinyAsciiWriter, TinyUnicodeWriter, KeyShortAsciiWriter

class SmileWriterSession(byteStringBuilder: ByteStringBuilder) : SmileWriter,
    HeaderWriter by HeaderWriterSession(byteStringBuilder),
    SmallIntegerWriter by SmallIntegerWriterSession(byteStringBuilder),
    StructuralWriter by StructuralWriterSession(byteStringBuilder),
    TinyAsciiWriter by TinyAsciiWriterSession(byteStringBuilder),
    TinyUnicodeWriter by TinyUnicodeWriterSession(byteStringBuilder),
    KeyShortAsciiWriter by KeyShortAsciiWriterSession(byteStringBuilder)
