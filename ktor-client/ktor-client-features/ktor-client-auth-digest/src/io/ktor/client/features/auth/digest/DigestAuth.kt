package io.ktor.client.features.auth.digest

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.util.*

class DigestAuth {

    class Config

    companion object Feature : HttpClientFeature<Config, DigestAuth> {
        override val key: AttributeKey<DigestAuth> = AttributeKey("DigestAuth")

        override fun prepare(block: Config.() -> Unit): DigestAuth {
            return DigestAuth()
        }

        override fun install(feature: DigestAuth, scope: HttpClient) {
            scope.requestPipeline.intercept(HttpRequestPipeline.State) {

            }
        }

    }
}