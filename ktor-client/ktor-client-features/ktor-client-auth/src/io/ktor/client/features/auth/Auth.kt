package io.ktor.client.features.auth

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.auth.*
import io.ktor.http.content.*
import io.ktor.pipeline.*
import io.ktor.util.*

interface AuthProvider {
    fun isApplicable(auth: HttpAuthHeader): Boolean
    fun request(request: HttpRequestBuilder)
}

class Auth {
    class Config

    companion object Feature : HttpClientFeature<Config, Auth> {
        override val key: AttributeKey<Auth> = AttributeKey("DigestAuth")

        private val Auth = PipelinePhase("AuthPhase")

        override fun prepare(block: Config.() -> Unit): Auth {
            return Auth()
        }

        override fun install(feature: Auth, scope: HttpClient) {
            scope.requestPipeline.insertPhaseBefore(HttpRequestPipeline.Render, Auth)

            scope.requestPipeline.intercept(Auth) { body ->
                if (body !is OutgoingContent) return@intercept

                val call = scope.sendPipeline.execute(context, body) as HttpEngineCall
                if (call.response.status != HttpStatusCode.Unauthorized) {
                    proceedWith(call)
                    return@intercept
                }

                val authHeader = call.response.headers[HttpHeaders.WWWAuthenticate]
            }
        }
    }
}