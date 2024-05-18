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
@Measurement(iterations = 5, time = 7, timeUnit = BenchmarkTimeUnit.SECONDS)
@Warmup(iterations = 5, time = 5, timeUnit = BenchmarkTimeUnit.SECONDS)
@OutputTimeUnit(BenchmarkTimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.Throughput)
@Fork(1)
class JacksonSmileBenchmark {

    private lateinit var largeMessage: LargeSmileMessage
    private lateinit var serializedLarge: ByteArray

    private lateinit var smallMessage: SmallSmileMessage
    private lateinit var serializedSmall: ByteArray

    private val smileMapper = ObjectMapper(
        SmileFactory.builder()
//            .configure(SmileGenerator.Feature.WRITE_HEADER, false)
            .configure(SmileGenerator.Feature.CHECK_SHARED_STRING_VALUES, false)
            .configure(SmileGenerator.Feature.CHECK_SHARED_NAMES, false)
            .build()
    ).findAndRegisterModules()

    @Setup
    fun setUp() {
        largeMessage = LargeSmileMessage.next()
        smallMessage = SmallSmileMessage.next()
        serializedLarge = Smile.encode(largeMessage)
        serializedSmall = Smile.encode(smallMessage)
    }

    @Benchmark
    fun kotlinSerializeLarge(): ByteArray {
        return Smile.encode(largeMessage)
    }

    @Benchmark
    fun kotlinSerializeSmall(): ByteArray {
        return Smile.encode(smallMessage)
    }

    @Benchmark
    fun kotlinDeserializeLarge(): LargeSmileMessage {
        return Smile.decode(serializedLarge)
    }

    @Benchmark
    fun kotlinDeserializeSmall(): SmallSmileMessage {
        return Smile.decode(serializedSmall)
    }

    @Benchmark
    fun jacksonSerializeLarge(): ByteArray {
        return smileMapper.writeValueAsBytes(largeMessage)
    }

    @Benchmark
    fun jacksonSerializeSmall(): ByteArray {
        return smileMapper.writeValueAsBytes(smallMessage)
    }

    @Benchmark
    fun jacksonDeserializeLarge(): LargeSmileMessage {
        return smileMapper.readValue(serializedLarge, LargeSmileMessage::class.java)
    }

    @Benchmark
    fun jacksonDeserializeSmall(): SmallSmileMessage {
        return smileMapper.readValue(serializedSmall, SmallSmileMessage::class.java)
    }
}
