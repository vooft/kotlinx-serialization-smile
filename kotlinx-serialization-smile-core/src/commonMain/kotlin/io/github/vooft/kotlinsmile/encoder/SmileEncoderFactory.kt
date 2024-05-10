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
import io.github.vooft.kotlinsmile.encoder.values.ValueLongStringWriter
import io.github.vooft.kotlinsmile.encoder.values.ValueLongStringWriterSession
import io.github.vooft.kotlinsmile.encoder.values.ValueShortStringWriter
import io.github.vooft.kotlinsmile.encoder.values.ValueShortStringWriterSession

class SmileEncoderFactory {
    fun write(block: SmileWriter.() -> Unit): ByteArray = buildByteArray {
        SmileWriterSession(this).block()
    }
}

interface SmileWriter : HeaderWriter, SmallIntegerWriter, StructuralWriter, ValueShortStringWriter, KeyStringWriter, ValueLongStringWriter

class SmileWriterSession(builder: ByteArrayBuilder) : SmileWriter,
    HeaderWriter by HeaderWriterSession(builder),
    SmallIntegerWriter by SmallIntegerWriterSession(builder),
    StructuralWriter by StructuralWriterSession(builder),
    ValueShortStringWriter by ValueShortStringWriterSession(builder),
    KeyStringWriter by KeyStringWriterSession(builder),
    ValueLongStringWriter by ValueLongStringWriterSession(builder)
