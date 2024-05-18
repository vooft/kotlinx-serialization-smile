jacksonBenchmark-macos:
	./gradlew :kotlinx-serialization-smile-benchmark:jvmBenchmarkJar
	java -jar kotlinx-serialization-smile-benchmark/build/benchmarks/jvm/jars/kotlinx-serialization-smile-benchmark-jvm-jmh-1.0-SNAPSHOT-JMH.jar \
 		-prof async:libPath=$(shell pwd)/kotlinx-serialization-smile-benchmark/libasyncProfiler/libasyncProfiler.dylib\;output=jfr\;dir=profile-results io.github.vooft.kotlinsmile.JacksonSmileBenchmar
