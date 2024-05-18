![Build and test](https://github.com/vooft/kotlinx-serialization-smile/actions/workflows/build.yml/badge.svg?branch=main)
![Releases](https://img.shields.io/github/v/release/vooft/kotlinx-serialization-smile)
![Maven Central](https://img.shields.io/maven-central/v/io.github.vooft/kotlinx-serialization-smile-core)
![License](https://img.shields.io/github/license/vooft/kotlinx-serialization-smile)

# kotlinx-serialization-smile
Kotlin Multiplatform library to support [Smile](https://en.wikipedia.org/wiki/Smile_(data_interchange_format)) format for `kotlinx.serialization`.

## Smile format
`Smile` is an efficient JSON-compatible binary data format, initially developed by Jackson JSON processor project team. Its logical data model is same as that of JSON, so it can be considered a "Binary JSON" format.

Specification for the format defined in [Smile Format Specification](https://github.com/FasterXML/smile-format-specification/tree/master).

<details>
<summary>Why use Smile?</summary>

Jackson maintainer Tatu Saloranta wrote an email describing different binary alternatives to JSON: https://groups.google.com/g/jackson-user/c/oQyF4Rit5lw/m/LxP33PbWn9EJ

One of the major products using Smile is ElasticSearch, that can use Smile and JSON interchangeably, and by using Smile you get both better performance and smaller stored data size: https://medium.com/its-tinkoff/elasticsearch-with-a-smile-d105f4b60d83

Performance comparison on the same data as the benchmark in this library:
```
Benchmark                                         Mode  Cnt    Score    Error   Units
JacksonJsonSmileBenchmark.jsonLargeDeserialize   thrpt    5   79.037 ±  2.253  ops/ms
JacksonJsonSmileBenchmark.smileLargeDeserialize  thrpt    5  117.915 ±  0.301  ops/ms

JacksonJsonSmileBenchmark.jsonLargeSerialize     thrpt    5  154.320 ±  0.421  ops/ms
JacksonJsonSmileBenchmark.smileLargeSerialize    thrpt    5  252.946 ±  0.717  ops/ms

JacksonJsonSmileBenchmark.jsonSmallDeserialize   thrpt    5  172.191 ± 11.006  ops/ms
JacksonJsonSmileBenchmark.smileSmallDeserialize  thrpt    5  226.521 ± 18.704  ops/ms

JacksonJsonSmileBenchmark.jsonSmallSerialize     thrpt    5  407.084 ±  3.078  ops/ms
JacksonJsonSmileBenchmark.smileSmallSerialize    thrpt    5  616.633 ±  2.665  ops/ms
```
</details>

# Quick start
Library is published to Maven Central under name [io.github.vooft:kotlinx-serialization-smile-core](https://central.sonatype.com/search?namespace=io.github.vooft).

Add the dependency to your project:
```kotlin
kotlin {
    ...

    sourceSets {
        commonMain.dependencies {
            implementation("io.github.vooft:kotlinx-serialization-smile-core:<version>")
        }
    }
}
```

Then in your code just create serializable classes as normal and use class `io.github.vooft.kotlinsmile.Smile` to encode/decode them.

```kotlin
@kotlinx.serialization.Serializable
data class Test(val name: String, val number: Int)

fun main() {
    val obj = Test("test", 42)

    // encode object to ByteArray
    val encoded = Smile.encode(obj)

    // decode ByteArray to object
    val decoded = Smile.decode<Test>(encoded)
}
```

This will use the default configuration.

## Supported features

The goal is to support all the features defined in the Smile specification, 
at the moment library can encode and decode almost all messages 
that produced by the default Jackson ObjectMapper configuration (exceptions are noted below).

### Supported data types
* ✅ Structural types
  * ✅ Classes (incl. nested classes)
  * ✅ Arrays
  * ✅ Collections (List, Set)
  * ✅ Maps
* ✅ Simple literals (empty string, null, boolean)
* ✅ Enums
* ❗Integers
  * ✅ Small integers (values in range [-16..15])
  * ✅ 32-bit integers
  * ✅ 64-bit integers
  * ❌ BigInteger
* ❗Floating-point numbers
  * ✅ 32-bit Float
  * ✅ 64-bit Double
  * ❌ BigDecimal
* ✅ Strings
  * ✅ Tiny/Short/Long Ascii
  * ✅ Tiny/Short/Long Unicode
* ✅ Binary data (only safe 7-bit encoding)
* ✅ Shared string references
  * ✅ Value short/long reference
  * ✅ Key short/long reference

### Supported encoding/decoding features
* ✅ Shared property names
* ✅ Shared string values
* ❌ Raw binary data (by default 7-bit encoding is used, this flag enables unescaped 8-bit encoding)
* ❌ Optional header (library always expects header and always writes one)

There is no standard `BigInteger` or `BigDecimal` implementation in Kotlin, potentially can use [kotlin-multiplatform-bignum](https://github.com/ionspin/kotlin-multiplatform-bignum) library.

# Performance

Library is designed to be fast and efficient, a benchmark is available in the [kotlinx-serialization-smile-benchmark](kotlinx-serialization-smile-benchmark) module.
It is slightly less performant on JVM than Jackson (executed on M2 Macbook Pro):
```
Benchmark                                             Mode  Cnt    Score    Error   Units
JacksonSmileBenchmark.jacksonDeserializeLarge        thrpt    5  117.358 ±  0.245  ops/ms
JacksonSmileBenchmark.kotlinDeserializeLarge         thrpt    5   95.336 ±  1.081  ops/ms

JacksonSmileBenchmark.jacksonDeserializeSmall        thrpt    5  223.094 ± 12.224  ops/ms
JacksonSmileBenchmark.kotlinDeserializeSmall         thrpt    5  236.330 ± 16.425  ops/ms

JacksonSmileBenchmark.jacksonSerializeLarge          thrpt    5  250.316 ±  1.529  ops/ms
JacksonSmileBenchmark.kotlinSerializeLarge           thrpt    5  203.799 ±  0.457  ops/ms

JacksonSmileBenchmark.jacksonSerializeSmall          thrpt    5  623.447 ±  2.316  ops/ms
JacksonSmileBenchmark.kotlinSerializeSmall           thrpt    5  521.293 ±  0.785  ops/ms
```
