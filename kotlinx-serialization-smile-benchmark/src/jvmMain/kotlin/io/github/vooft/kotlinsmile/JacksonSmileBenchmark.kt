package io.github.vooft.kotlinsmile

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.smile.SmileFactory
import com.fasterxml.jackson.dataformat.smile.SmileGenerator
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
@Measurement(iterations = 5, time = 5, timeUnit = BenchmarkTimeUnit.SECONDS)
@Warmup(iterations = 3, time = 5, timeUnit = BenchmarkTimeUnit.SECONDS)
@OutputTimeUnit(BenchmarkTimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.Throughput)
@Fork(1)
class JacksonSmileBenchmark {

    private lateinit var largeMessage: LargeSmileMessage
    private lateinit var serializedLarge: ByteArray

    private lateinit var smallMessage: SmallSmileMessage
    private lateinit var serializedSmall: ByteArray

    private val jackson = ObjectMapper(
        SmileFactory.builder()
            .configure(SmileGenerator.Feature.CHECK_SHARED_STRING_VALUES, true)
            .configure(SmileGenerator.Feature.CHECK_SHARED_NAMES, true)
            .build()
    ).findAndRegisterModules()

    private val smile = Smile(SmileConfig(shareStringValue = true, sharePropertyName = true))

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
