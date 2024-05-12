package io.github.vooft.kotlinsmile.adapter.decoder.common

import io.github.vooft.kotlinsmile.common.ByteArrayIterator
import io.github.vooft.kotlinsmile.token.SmileKeyToken
import io.github.vooft.kotlinsmile.token.SmileToken
import io.github.vooft.kotlinsmile.token.SmileValueToken

fun ByteArrayIterator.peekValueToken(): SmileValueToken {
    val byte = next()
    rollback(1)
    return SmileToken.valueToken(byte) ?: error("Unknown key token: ${byte.toUByte().toString(16)}")
}

fun ByteArrayIterator.peekKeyToken(): SmileKeyToken {
    val byte = next()
    rollback(1)
    return SmileToken.keyToken(byte) ?: error("Unknown key token: ${byte.toUByte().toString(16)}")
}

fun ByteArrayIterator.nextUByte() = next().toUInt() and 0xFFu
