package com.asturiancoder.photofeed.feed.api

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.runBlocking
import io.ktor.client.statement.HttpResponse as KtorHttpResponse

internal class KtorHttpClient(
    private val client: io.ktor.client.HttpClient
) : HttpClient {

    override fun get(url: String): Result<HttpResponse> = runBlocking {
        runCatching { client.get(url).map() }
    }

    private suspend fun KtorHttpResponse.map() =
        HttpResponse(code = status.value, jsonString = bodyAsText())
}