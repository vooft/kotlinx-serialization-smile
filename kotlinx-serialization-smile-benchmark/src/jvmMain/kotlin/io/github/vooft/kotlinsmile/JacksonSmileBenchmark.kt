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
import kotlin.random.Random

@State(Scope.Benchmark)
@Measurement(iterations = 3, time = 7, timeUnit = BenchmarkTimeUnit.SECONDS)
@Warmup(iterations = 2, time = 5, timeUnit = BenchmarkTimeUnit.SECONDS)
@OutputTimeUnit(BenchmarkTimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.Throughput)
class JacksonSmileBenchmark {

    private lateinit var logMessage: LogMessage
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
        logMessage = LogMessage(
            timestampEpoch = Random.nextLong(),
            level = "INFO",
            message = "Hello, world!",
            threadName = "main",
            loggerName = "io.github.vooft.kotlinsmile.KotlinSmileBenchmark",
            traceId = "1234567890",
            spanId = "1234567890",
            context = LogMessageContext(
                username = "admin",
                url = "http://localhost:8080",
                userAgent = "Mozilla/5.0"
            )
        )

        serialized = Smile.encode(logMessage)
    }

    @Benchmark
    fun kotlinSerialize(): ByteArray {
        return Smile.encode(logMessage)
    }

    @Benchmark
    fun kotlinDeserialize(): LogMessage {
        return Smile.decode(serialized)
    }

    @Benchmark
    fun jacksonSerialize(): ByteArray {
        return smileMapper.writeValueAsBytes(logMessage)
    }

    @Benchmark
    fun jacksonDeserialize(): LogMessage {
        return smileMapper.readValue(serialized, LogMessage::class.java)
    }
}

