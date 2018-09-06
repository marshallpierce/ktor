package io.ktor.http.auth

enum class HeaderValueEncoding {
    QUOTED_WHEN_REQUIRED,
    QUOTED_ALWAYS,
    URI_ENCODE
}
