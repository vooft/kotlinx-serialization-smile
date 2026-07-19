package io.github.vooft.kotlinsmile

import tools.jackson.dataformat.smile.SmileFactory
import tools.jackson.dataformat.smile.SmileMapper
import tools.jackson.dataformat.smile.SmileWriteFeature
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.BenchmarkMode
import kotlinx.benchmark.BenchmarkTimeUnit
import kotlinx.benchmark.Measurement
import kotlinx.benchmark.Mode
import kotlinx.benchmark.OutputTimeUnit
import kotlinx.benchmark.Scope
import kotlinx.benchmark.Setup
import kotlinx.benchmark.State
import kotlinx.benchmark.Warmup
import org.openjdk.jmh.annotations.Fork

@State(Scope.Benchmark)
@Measurement(iterations = 5, time = 7, timeUnit = BenchmarkTimeUnit.SECONDS)
@Warmup(iterations = 5, time = 5, timeUnit = BenchmarkTimeUnit.SECONDS)
@OutputTimeUnit(BenchmarkTimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.Throughput)
@Fork(2)
class JacksonSmileBenchmark {

    private lateinit var largeMessage: LargeSmileMessage
    private lateinit var serializedLarge: ByteArray

    private lateinit var smallMessage: SmallSmileMessage
    private lateinit var serializedSmall: ByteArray

    private val jackson = SmileMapper.builder(
        SmileFactory.builder()
            .configure(SmileWriteFeature.CHECK_SHARED_STRING_VALUES, true)
            .configure(SmileWriteFeature.CHECK_SHARED_NAMES, true)
            .build()
    ).findAndAddModules().build()

    private val smile = Smile(SmileConfig(shareValues = true, shareKeys = true))

    @Setup
    fun setUp() {
        largeMessage = LargeSmileMessage.next()
        smallMessage = SmallSmileMessage.next()
        serializedLarge = smile.encode(largeMessage)
        serializedSmall = smile.encode(smallMessage)
    }

    @Benchmark
    fun largeSerializeKotlin(): ByteArray {
        return smile.encode(largeMessage)
    }

    @Benchmark
    fun smallSerializeKotlin(): ByteArray {
        return smile.encode(smallMessage)
    }

    @Benchmark
    fun largeDeserializeKotlin(): LargeSmileMessage {
        return smile.decode(serializedLarge)
    }

    @Benchmark
    fun smallDeserializeKotlin(): SmallSmileMessage {
        return smile.decode(serializedSmall)
    }

    @Benchmark
    fun largeSerializeJackson(): ByteArray {
        return jackson.writeValueAsBytes(largeMessage)
    }

    @Benchmark
    fun smallSerializeJackson(): ByteArray {
        return jackson.writeValueAsBytes(smallMessage)
    }

    @Benchmark
    fun largeDeserializeJackson(): LargeSmileMessage {
        return jackson.readValue(serializedLarge, LargeSmileMessage::class.java)
    }

    @Benchmark
    fun smallDeserializeJackson(): SmallSmileMessage {
        return jackson.readValue(serializedSmall, SmallSmileMessage::class.java)
    }
}
