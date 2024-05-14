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

@State(Scope.Benchmark)
@Measurement(iterations = 5, time = 7, timeUnit = BenchmarkTimeUnit.SECONDS)
@Warmup(iterations = 5, time = 5, timeUnit = BenchmarkTimeUnit.SECONDS)
@OutputTimeUnit(BenchmarkTimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.Throughput)
class JacksonSmileBenchmark {

    private lateinit var message: SmileMessage
    private lateinit var serialized: ByteArray

    private val smileMapper = ObjectMapper(
        SmileFactory.builder()
//            .configure(SmileGenerator.Feature.WRITE_HEADER, false)
            .configure(SmileGenerator.Feature.CHECK_SHARED_STRING_VALUES, false)
            .configure(SmileGenerator.Feature.CHECK_SHARED_NAMES, false)
            .build()
    ).findAndRegisterModules()

    @Setup
    fun setUp() {
        message = SmileMessage.next()
        serialized = Smile.encode(message)
    }

    @Benchmark
    fun kotlinSerialize(): ByteArray {
        return Smile.encode(message)
    }

    @Benchmark
    fun kotlinDeserialize(): SmileMessage {
        return Smile.decode(serialized)
    }

    @Benchmark
    fun jacksonSerialize(): ByteArray {
        return smileMapper.writeValueAsBytes(message)
    }

    @Benchmark
    fun jacksonDeserialize(): SmileMessage {
        return smileMapper.readValue(serialized, SmileMessage::class.java)
    }
}
