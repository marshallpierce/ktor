package io.ktor.http.auth

internal val authSchemePattern = "\\S+".toRegex()

object AuthScheme {
    val Basic = "Basic"
    val Digest = "Digest"
    val Negotiate = "Negotiate"
    val OAuth = "OAuth"
}
