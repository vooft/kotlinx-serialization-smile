package io.github.vooft.kotlinsmile.encoder

import io.github.vooft.kotlinsmile.common.ByteArrayBuilder
import io.github.vooft.kotlinsmile.common.shared.SmileSharedStorage
import io.github.vooft.kotlinsmile.encoder.keys.DecidingSharedKeyStringWriter
import io.github.vooft.kotlinsmile.encoder.keys.KeyStringWriter
import io.github.vooft.kotlinsmile.encoder.keys.KeyStringWriterSession
import io.github.vooft.kotlinsmile.encoder.keys.SharedKeyStringWriter
import io.github.vooft.kotlinsmile.encoder.keys.SharedKeyStringWriterSession
import io.github.vooft.kotlinsmile.encoder.structure.HeaderWriter
import io.github.vooft.kotlinsmile.encoder.structure.HeaderWriterSession
import io.github.vooft.kotlinsmile.encoder.structure.StructuralWriter
import io.github.vooft.kotlinsmile.encoder.structure.StructuralWriterSession
import io.github.vooft.kotlinsmile.encoder.values.BinaryWriter
import io.github.vooft.kotlinsmile.encoder.values.BinaryWriterSession
import io.github.vooft.kotlinsmile.encoder.values.DecidingValueShortStringWriter
import io.github.vooft.kotlinsmile.encoder.values.FloatWriter
import io.github.vooft.kotlinsmile.encoder.values.FloatWriterSession
import io.github.vooft.kotlinsmile.encoder.values.IntegerWriter
import io.github.vooft.kotlinsmile.encoder.values.IntegerWriterSession
import io.github.vooft.kotlinsmile.encoder.values.SharedValueShortStringWriterSession
import io.github.vooft.kotlinsmile.encoder.values.ValueLongStringWriter
import io.github.vooft.kotlinsmile.encoder.values.ValueLongStringWriterSession
import io.github.vooft.kotlinsmile.encoder.values.ValueShortStringWriter
import io.github.vooft.kotlinsmile.encoder.values.ValueShortStringWriterSession
import io.github.vooft.kotlinsmile.encoder.values.ValueSimpleLiteralWriter
import io.github.vooft.kotlinsmile.encoder.values.ValueSimpleLiteralWriterSession

class SmileEncoderSession(private val builder: ByteArrayBuilder, private val sharedStorage: SmileSharedStorage) :
    HeaderWriter by HeaderWriterSession(builder),
    IntegerWriter by IntegerWriterSession(builder),
    FloatWriter by FloatWriterSession(builder),
    StructuralWriter by StructuralWriterSession(builder),
    KeyStringWriter by DecidingSharedKeyStringWriter(
        delegate = KeyStringWriterSession(builder = builder, sharedStorage = sharedStorage),
        sharedKeyStringWriter = SharedKeyStringWriterSession(builder = builder, sharedStorage = sharedStorage)
    ),
    ValueShortStringWriter by DecidingValueShortStringWriter(
        delegate = ValueShortStringWriterSession(builder, sharedStorage),
        sharedValueShortStringWriter = SharedValueShortStringWriterSession(builder = builder, sharedStorage = sharedStorage)
    ),
    ValueLongStringWriter by ValueLongStringWriterSession(builder),
    ValueSimpleLiteralWriter by ValueSimpleLiteralWriterSession(builder),
    BinaryWriter by BinaryWriterSession(builder),
    SharedKeyStringWriter by SharedKeyStringWriterSession(builder, sharedStorage) {

    fun toByteArray(): ByteArray = builder.toByteArray()
}
