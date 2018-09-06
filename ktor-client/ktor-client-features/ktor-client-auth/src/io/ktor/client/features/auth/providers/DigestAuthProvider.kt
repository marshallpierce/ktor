package io.ktor.client.features.auth.providers

import io.ktor.client.features.auth.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.auth.*
import io.ktor.util.*
import kotlinx.atomicfu.*

class DigestAuthProvider(
    val username: String,
    val password: String,
    val realm: String?,
    val algorithmName: String = "MD5"
) : AuthProvider {
    private val digest: Digest = Digest(algorithmName)
    private val serverNonce = atomic<String?>(null)
    private val qop = atomic<String?>(null)
    private val opaque = atomic<String?>(null)
    private val clientNonce = generateNonce()

    private val requestCounter = atomic(0)

    override fun isApplicable(auth: HttpAuthHeader): Boolean {
        if (auth !is HttpAuthHeader.Parameterized ||
            auth.parameter("realm") != realm ||
            auth.authScheme != AuthScheme.Digest
        ) return false

        val newNonce = auth.parameter("nonce") ?: return false
        val newQop = auth.parameter("qop")
        val newOpaque = auth.parameter("opaque")

        serverNonce.value = newNonce
        qop.value = newQop
        opaque.value = newOpaque

        return true
    }

    override fun addRequestHeaders(request: HttpRequestBuilder) {
        val methodName = request.method.value
        val url = URLBuilder().takeFrom(request.url).build()

        val nonce = serverNonce.value!!
        val serverOpaque = opaque.value!!

        val credential = digest.build("$username:$realm:$password")

        val HA1 = when (algorithmName) {
            "MD5-sess" -> digest.build("$credential:$nonce:$clientNonce")
            else -> credential
        }

        val actualQop = qop.value

        val HA2 = when (actualQop) {
            "auth-int" -> digest.build("$methodName:$url:$")
            else -> digest.build("$methodName:${url.encodedPath}")
        }

        val nonceCount = requestCounter.incrementAndGet()
        val response = when (actualQop) {
            "auth-int", "auth" -> digest.build("$HA1:$nonce:$nonceCount:$clientNonce:$actualQop:$HA2")
            else -> digest.build("$HA1:$nonce:$HA2")
        }

        val auth = HttpAuthHeader.Parameterized(AuthScheme.Digest, linkedMapOf<String, String>().apply {
            realm?.let { this["realm"] = it }
            this["opaque"] = serverOpaque
            this["nonce"] = nonce
            this["cnonce"] = clientNonce
            this["response"] = hex(response)
            this["uri"] = url.encodedPath
        })

        request.headers {
            append(HttpHeaders.Authorization, auth.render())
        }
    }
}

class DigestAuthConfig {
}

fun Auth.digest(block: DigestAuthConfig.() -> Unit) {
}
