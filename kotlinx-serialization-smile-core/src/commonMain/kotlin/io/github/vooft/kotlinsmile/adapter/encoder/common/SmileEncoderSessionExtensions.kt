package io.github.vooft.kotlinsmile.adapter.encoder.common

import io.github.vooft.kotlinsmile.common.SmileString
import io.github.vooft.kotlinsmile.common.isAscii
import io.github.vooft.kotlinsmile.common.isEmpty
import io.github.vooft.kotlinsmile.common.isUnicode
import io.github.vooft.kotlinsmile.common.length
import io.github.vooft.kotlinsmile.encoder.SmileEncoderSession
import io.github.vooft.kotlinsmile.token.SmileKeyToken.KeyLongUnicode
import io.github.vooft.kotlinsmile.token.SmileKeyToken.KeyShortAscii
import io.github.vooft.kotlinsmile.token.SmileKeyToken.KeyShortUnicode
import io.github.vooft.kotlinsmile.token.SmileValueToken.LongInteger
import io.github.vooft.kotlinsmile.token.SmileValueToken.RegularInteger
import io.github.vooft.kotlinsmile.token.SmileValueToken.ShortAscii
import io.github.vooft.kotlinsmile.token.SmileValueToken.ShortUnicode
import io.github.vooft.kotlinsmile.token.SmileValueToken.SmallInteger
import io.github.vooft.kotlinsmile.token.SmileValueToken.TinyAscii
import io.github.vooft.kotlinsmile.token.SmileValueToken.TinyUnicode

fun SmileEncoderSession.valueInteger(value: Long) = when (value) {
    in SmallInteger.values -> smallInteger(value.toInt())
    in RegularInteger.values -> regularInteger(value.toInt())
    in LongInteger.values -> longInteger(value)
    else -> error("Invalid value $value is too big")
}

fun SmileEncoderSession.valueString(value: SmileString) {
    if (value.isEmpty) {
        emptyString()
    } else if (value.isAscii) {
        when (value.length) {
            in TinyAscii.lengths -> valueTinyAscii(value)
            in ShortAscii.lengths -> valueShortAscii(value)
            else -> valueLongAscii(value)
        }
    } else {
        when (value.length) {
            in TinyUnicode.lengths -> valueTinyUnicode(value)
            in ShortUnicode.lengths -> valueShortUnicode(value)
            else -> valueLongUnicode(value)
        }
    }
}

fun SmileEncoderSession.keyString(value: SmileString) {
    when {
        value.length in KeyLongUnicode.BYTE_LENGTHS -> keyLongUnicode(value)
        value.isUnicode && value.length in KeyShortUnicode.BYTE_LENGTHS -> keyShortUnicode(value)
        value.isAscii && value.length in KeyShortAscii.BYTE_LENGTHS -> keyShortAscii(value)
        else -> error("Element name $value is too long")
    }
}
