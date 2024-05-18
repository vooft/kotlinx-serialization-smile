package io.github.vooft.kotlinsmile.decoder.raw

import io.github.vooft.kotlinsmile.common.ByteArrayIterator
import io.github.vooft.kotlinsmile.token.SmileMarkers

fun ByteArrayIterator.nextRawLongString(): String {
    var counter = 0
    while (next() != SmileMarkers.STRING_END_MARKER) {
        counter++
    }

    rollback(counter + 1)

    val decoded = nextString(counter)
    require(next() == SmileMarkers.STRING_END_MARKER) { "Invalid end marker for long unicode key" }

    return decoded
}
