package com.asturiancoder.photofeed

import com.asturiancoder.photofeed.feed.api.HttpClient
import com.asturiancoder.photofeed.feed.api.HttpResponse
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.get
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import io.ktor.client.HttpClient as KtorClient

class KtorHttpClient(
    private val client: KtorClient
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

class KtorHttpClientTests {

    @Test
    fun get_performsGetRequestWithUrl() {
        val url = anyUrl()
        var receivedRequest: HttpRequestData? = null
        val engine = MockEngine {
            receivedRequest = it
            respond("")
        }
        val sut = KtorHttpClient(KtorClient(engine))

        sut.get(url)

        assertEquals(url, receivedRequest!!.url.toString())
        assertEquals("GET", receivedRequest!!.method.value)
    }

    @Test
    fun get_failsOnRequestError() {
        val requestError = Exception("Request error")
        val engine = MockEngine { throw requestError }
        val sut = KtorHttpClient(KtorClient(engine))

        val result = sut.get(anyUrl())

        assertNotNull(result.exceptionOrNull(), "Expected failure, got $result instead")
        assertEquals(requestError.message, result.exceptionOrNull()?.message)
    }

    // region Helpers

    private fun anyUrl() = "http://any-url.com"

    // endregion
}