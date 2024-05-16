package io.github.vooft.kotlinsmile.adapter.decoder.common

import io.github.vooft.kotlinsmile.decoder.SmileDecoderSession
import io.github.vooft.kotlinsmile.token.SmileKeyToken.KeyLongUnicode
import io.github.vooft.kotlinsmile.token.SmileKeyToken.KeyShortAscii
import io.github.vooft.kotlinsmile.token.SmileKeyToken.KeyShortUnicode
import io.github.vooft.kotlinsmile.token.SmileKeyToken.LongSharedKey
import io.github.vooft.kotlinsmile.token.SmileKeyToken.ShortSharedKey
import io.github.vooft.kotlinsmile.token.SmileKeyToken.SmileKeyStringToken
import io.github.vooft.kotlinsmile.token.SmileValueToken.LongAscii
import io.github.vooft.kotlinsmile.token.SmileValueToken.LongInteger
import io.github.vooft.kotlinsmile.token.SmileValueToken.LongSharedValue
import io.github.vooft.kotlinsmile.token.SmileValueToken.LongUnicode
import io.github.vooft.kotlinsmile.token.SmileValueToken.RegularInteger
import io.github.vooft.kotlinsmile.token.SmileValueToken.ShortAscii
import io.github.vooft.kotlinsmile.token.SmileValueToken.ShortSharedValue
import io.github.vooft.kotlinsmile.token.SmileValueToken.ShortUnicode
import io.github.vooft.kotlinsmile.token.SmileValueToken.SimpleLiteralEmptyString
import io.github.vooft.kotlinsmile.token.SmileValueToken.SmallInteger
import io.github.vooft.kotlinsmile.token.SmileValueToken.SmileStringToken
import io.github.vooft.kotlinsmile.token.SmileValueToken.TinyAscii
import io.github.vooft.kotlinsmile.token.SmileValueToken.TinyUnicode


fun SmileDecoderSession.valueInt(): Long {
    return when (val token = peekValueToken()) {
        SmallInteger -> valueSmallInteger().toLong()
        RegularInteger -> valueRegularInteger().toLong()
        LongInteger -> valueLongInteger()
        else -> error("Unexpected token $token")
    }
}

fun SmileDecoderSession.valueString(): String {
    val token = peekValueToken()
    println("decoding $token")
    return when (token) {
        LongAscii -> valueLongAscii()
        SimpleLiteralEmptyString -> valueEmptyString()
        LongUnicode -> valueLongUnicode()
        ShortAscii -> valueShortAscii()
        ShortUnicode -> valueShortUnicode()
        TinyAscii -> valueTinyAscii()
        TinyUnicode -> valueTinyUnicode()
        ShortSharedValue -> shortSharedValue()
        LongSharedValue -> longSharedValue()
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
    val token = peekKeyToken()
    require(token is SmileKeyStringToken) { "Unexpected token $token" }
    return when (token) {
        KeyLongUnicode -> keyLongUnicode()
        KeyShortAscii -> keyShortAscii()
        KeyShortUnicode -> keyShortUnicode()
        ShortSharedKey -> shortSharedKey()
        LongSharedKey -> longSharedKey()
    }
}
