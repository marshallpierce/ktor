package io.ktor.auth

import io.ktor.http.auth.*
import io.ktor.request.*

fun ApplicationRequest.parseAuthorizationHeader(): HttpAuthHeader? = authorization()?.let {
    parseAuthorizationHeader(it)
}
