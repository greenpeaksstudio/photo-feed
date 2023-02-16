package com.asturiancoder.photofeed.feed.api

import com.asturiancoder.photofeed.feed.api.model.HttpResponse
import com.asturiancoder.photofeed.feed.api.model.HttpStatusCode
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.runBlocking
import io.ktor.client.HttpClient as KtorClient
import io.ktor.client.statement.HttpResponse as KtorHttpResponse

class KtorHttpClient(
    private val client: KtorClient
) : HttpClient {

    override fun get(url: String): Result<HttpResponse> = runBlocking {
        runCatching { client.get(url).map() }
    }

    private suspend fun KtorHttpResponse.map() =
        HttpResponse(code = HttpStatusCode.fromValue(status.value), jsonString = bodyAsText())
}