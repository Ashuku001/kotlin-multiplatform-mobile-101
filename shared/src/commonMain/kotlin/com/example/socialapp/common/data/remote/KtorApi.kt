package com.example.socialapp.common.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.headers
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.path
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val BASE_URL = "http://192.168.1.2:8080"

internal abstract class KtorApi {
    // constructor
    val client = HttpClient {
        install(ContentNegotiation) {
            json(
                Json{
                    ignoreUnknownKeys = false
                    useAlternativeNames = false
                }
            )
        }
    }

    // an extension to RequestBuilder called endpoint
    fun HttpRequestBuilder.endpoint(path: String) {
        url {
            takeFrom(BASE_URL)
            path(path)
            contentType(ContentType.Application.Json)
        }
    }

    // an extension to add tokens to the request headers
    fun HttpRequestBuilder.setToken(token: String) {
        headers{
            append(name = "Authorization", value = "Bearer $token")
        }
    }

    fun HttpRequestBuilder.setupMultipartRequest() {
        contentType(ContentType.MultiPart.FormData)
    }
}
