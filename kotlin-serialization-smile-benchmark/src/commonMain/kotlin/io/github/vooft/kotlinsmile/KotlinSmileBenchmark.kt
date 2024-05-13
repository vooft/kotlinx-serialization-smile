package io.github.vooft.kotlinsmile

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
class KotlinSmileBenchmark {
    private lateinit var logMessage: LogMessage
    private lateinit var serialized: ByteArray

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
    fun serialize(): ByteArray {
        return Smile.encode(logMessage)
    }

    @Benchmark
    fun deserialize(): LogMessage {
        return Smile.decode(serialized)
    }
}

