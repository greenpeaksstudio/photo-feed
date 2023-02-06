package com.asturiancoder.photofeed.feed.api

import io.ktor.client.request.get
import kotlinx.coroutines.runBlocking

internal class KtorHttpClient(
    private val client: io.ktor.client.HttpClient
) : HttpClient {

    override fun get(url: String): Result<HttpResponse> = runBlocking {
        try {
            client.get(url)
        } catch (exception: Exception) {
            return@runBlocking Result.failure(exception)
        }

        return@runBlocking Result.failure(Exception())
    }
}