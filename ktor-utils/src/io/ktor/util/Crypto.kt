package io.ktor.util

import kotlinx.io.charsets.*
import kotlinx.io.core.*

fun hex(bytes: ByteArray): String = bytes.joinToString("") {
    (it.toInt() and 0xff).toString(16).padStart(2, '0')
}

expect fun generateNonce(): String

expect fun Digest(name: String): Digest

interface Digest {
    operator fun plusAssign(bytes: ByteArray)

    fun reset()

    fun build(): ByteArray
}

fun Digest.build(bytes: ByteArray): ByteArray {
    this += bytes
    return build()
}

fun Digest.build(string: String, charset: Charset = Charsets.UTF_8): ByteArray {
    this += string.toByteArray(charset)
    return build()
}


