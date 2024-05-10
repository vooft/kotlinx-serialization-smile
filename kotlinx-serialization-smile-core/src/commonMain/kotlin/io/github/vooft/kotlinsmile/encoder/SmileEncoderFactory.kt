package io.github.vooft.kotlinsmile.encoder

import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.github.vooft.kotlinsmile.common.buildByteArray
import io.github.vooft.kotlinsmile.encoder.keys.KeyStringWriter
import io.github.vooft.kotlinsmile.encoder.keys.KeyStringWriterSession
import io.github.vooft.kotlinsmile.encoder.structure.HeaderWriter
import io.github.vooft.kotlinsmile.encoder.structure.HeaderWriterSession
import io.github.vooft.kotlinsmile.encoder.structure.StructuralWriter
import io.github.vooft.kotlinsmile.encoder.structure.StructuralWriterSession
import io.github.vooft.kotlinsmile.encoder.values.SmallIntegerWriter
import io.github.vooft.kotlinsmile.encoder.values.SmallIntegerWriterSession
import io.github.vooft.kotlinsmile.encoder.values.ValueStringWriter
import io.github.vooft.kotlinsmile.encoder.values.ValueStringWriterSession

class SmileEncoderFactory {
    fun write(block: SmileWriter.() -> Unit): ByteArray = buildByteArray {
        SmileWriterSession(this).block()
    }
}

interface SmileWriter : HeaderWriter, SmallIntegerWriter, StructuralWriter, ValueStringWriter, KeyStringWriter

class SmileWriterSession(builder: ByteArrayBuilder) : SmileWriter,
    HeaderWriter by HeaderWriterSession(builder),
    SmallIntegerWriter by SmallIntegerWriterSession(builder),
    StructuralWriter by StructuralWriterSession(builder),
    ValueStringWriter by ValueStringWriterSession(builder),
    KeyStringWriter by KeyStringWriterSession(builder)
