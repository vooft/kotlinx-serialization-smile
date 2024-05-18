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

@State(Scope.Benchmark)
@Measurement(iterations = 5, time = 7, timeUnit = BenchmarkTimeUnit.SECONDS)
@Warmup(iterations = 5, time = 5, timeUnit = BenchmarkTimeUnit.SECONDS)
@OutputTimeUnit(BenchmarkTimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.Throughput)
class MultiplatformSmileBenchmark {

    private lateinit var smallMessage: SmallSmileMessage
    private lateinit var smallSerialized: ByteArray

    private lateinit var largeMessage: LargeSmileMessage
    private lateinit var largeSerialized: ByteArray

    @Setup
    fun setUp() {
        largeMessage = LargeSmileMessage.next()
        largeSerialized = Smile.encode(largeMessage)

        smallMessage = SmallSmileMessage.next()
        smallSerialized = Smile.encode(smallMessage)
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
        return Smile.decode(largeSerialized)
    }

    @Benchmark
    fun kotlinDeserializeSmall(): SmallSmileMessage {
        return Smile.decode(smallSerialized)
    }
}
