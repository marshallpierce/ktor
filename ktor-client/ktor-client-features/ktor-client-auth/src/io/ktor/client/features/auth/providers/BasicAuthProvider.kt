package io.ktor.client.features.auth.providers

import io.ktor.client.features.auth.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.auth.*
import io.ktor.util.*

class BasicAuthProvider(
    private val username: String,
    private val password: String,
    private val realm: String?
) : AuthProvider {

    override fun isApplicable(auth: HttpAuthHeader): Boolean {
        if (auth.authScheme != AuthScheme.Basic) return false

        if (realm != null) {
            if (auth !is HttpAuthHeader.Parameterized) return false
            return auth.parameter("realm") == realm
        }

        return true
    }

    override fun request(request: HttpRequestBuilder) {
        request.headers[HttpHeaders.Authorization] = constructBasicAuthValue(username, password)
    }

    private fun constructBasicAuthValue(username: String, password: String): String {
        val authString = "$username:$password"
        val authBuf = encodeBase64(authString.toByteArray(Charsets.ISO_8859_1))

        return "Basic $authBuf"
    }

}

class BasicAuthConfig {
    /**
     * Required: The username of the basic auth.
     */
    lateinit var username: String

    /**
     * Required: The password of the basic auth.
     */
    lateinit var password: String

    /**
     * Optional: current provider realm
     */
    var realm: String? = null
}

fun Auth.Config.basic(block: BasicAuthConfig.() -> Unit) {
}