package io.github.vooft.kotlinsmile.adapter.decoder.common

import io.github.vooft.kotlinsmile.decoder.SmileDecoderSession
import io.github.vooft.kotlinsmile.token.SmileKeyToken.KeyLongUnicode
import io.github.vooft.kotlinsmile.token.SmileKeyToken.KeyShortAscii
import io.github.vooft.kotlinsmile.token.SmileKeyToken.KeyShortUnicode
import io.github.vooft.kotlinsmile.token.SmileKeyToken.SmileKeyStringToken
import io.github.vooft.kotlinsmile.token.SmileValueToken.LongAscii
import io.github.vooft.kotlinsmile.token.SmileValueToken.LongUnicode
import io.github.vooft.kotlinsmile.token.SmileValueToken.ShortAscii
import io.github.vooft.kotlinsmile.token.SmileValueToken.ShortUnicode
import io.github.vooft.kotlinsmile.token.SmileValueToken.SimpleLiteralEmptyString
import io.github.vooft.kotlinsmile.token.SmileValueToken.SmallInteger
import io.github.vooft.kotlinsmile.token.SmileValueToken.SmileStringToken
import io.github.vooft.kotlinsmile.token.SmileValueToken.TinyAscii
import io.github.vooft.kotlinsmile.token.SmileValueToken.TinyUnicode

fun SmileDecoderSession.valueInt(): Int {
    return when (val token = peekValueToken()) {
        SmallInteger -> smallInteger().toInt()
        else -> error("Unexpected token $token")
    }
}

fun SmileDecoderSession.valueString(): String {
    return when (val token = peekValueToken()) {
        LongAscii -> valueLongAscii()
        SimpleLiteralEmptyString -> valueEmptyString()
        LongUnicode -> valueLongUnicode()
        ShortAscii -> valueShortAscii()
        ShortUnicode -> valueShortUnicode()
        TinyAscii -> valueTinyAscii()
        TinyUnicode -> valueTinyUnicode()
        else -> error("Unexpected token $token")
    }
}

fun SmileDecoderSession.peekStringValueToken(): SmileStringToken {
    return when (val token = peekValueToken()) {
        is SmileStringToken -> token
        else -> error("Unexpected token $token")
    }
}

fun SmileDecoderSession.peekStringKeyToken(): SmileKeyStringToken {
    return when (val token = peekKeyToken()) {
        is SmileKeyStringToken -> token
        else -> error("Unexpected token $token")
    }
}

fun SmileDecoderSession.keyString(): String {
    return when (val token = peekKeyToken()) {
        KeyLongUnicode -> keyLongUnicode()
        KeyShortAscii -> keyShortAscii()
        KeyShortUnicode -> keyShortUnicode()
        else -> error("Unexpected token $token")
    }
}
