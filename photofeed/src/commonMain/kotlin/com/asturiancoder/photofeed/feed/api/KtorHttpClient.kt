package com.asturiancoder.photofeed.feed.api

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.runBlocking

internal class KtorHttpClient(
    private val client: io.ktor.client.HttpClient
) : HttpClient {

    override fun get(url: String): Result<HttpResponse> = runBlocking {
        try {
            val response = client.get(url)
            val httpResponse = HttpResponse(code = response.status.value,
                response.bodyAsText())
            return@runBlocking Result.success(httpResponse)
        } catch (exception: Exception) {
            return@runBlocking Result.failure(exception)
        }
    }
}