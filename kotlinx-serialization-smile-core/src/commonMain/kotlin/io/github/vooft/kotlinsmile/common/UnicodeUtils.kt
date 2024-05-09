package io.github.vooft.kotlinsmile.common

fun Char.isAscii(): Boolean = code < 128

fun String.isAscii(): Boolean = all { it.isAscii() }
fun String.isUnicode(): Boolean = !isAscii()
