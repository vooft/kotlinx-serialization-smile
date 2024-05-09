package io.github.vooft.kotlinsmile.encoder

import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.github.vooft.kotlinsmile.common.buildByteArray
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

class SmileEncoderFactory {
    fun write(block: SmileWriter.() -> Unit): ByteArray = buildByteArray {
        SmileWriterSession(this).block()
    }
}

interface SmileWriter : HeaderWriter, SmallIntegerWriter, StructuralWriter, TinyAsciiWriter, TinyUnicodeWriter, KeyShortAsciiWriter

class SmileWriterSession(builder: ByteArrayBuilder) : SmileWriter,
    HeaderWriter by HeaderWriterSession(builder),
    SmallIntegerWriter by SmallIntegerWriterSession(builder),
    StructuralWriter by StructuralWriterSession(builder),
    TinyAsciiWriter by TinyAsciiWriterSession(builder),
    TinyUnicodeWriter by TinyUnicodeWriterSession(builder),
    KeyShortAsciiWriter by KeyShortAsciiWriterSession(builder)
