package com.greenpeaks.photofeed.feed.api

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import io.ktor.client.HttpClient as KtorClient

class KtorHttpClientTests {

    @Test
    fun get_performsGetRequestWithUrl() {
        val url = anyUrl
        val (sut, engine) = makeSut()

        sut.get(url)

        val receivedRequest = engine.requestHistory.first()
        assertEquals(url, receivedRequest.url.toString())
        assertEquals("GET", receivedRequest.method.value)
    }

    @Test
    fun get_failsOnRequestError() {
        val (sut, engine) = makeSut()
        val requestError = Exception("Request error")
        engine.completeWithError(requestError)

        val receivedError = assertFails { sut.get(anyUrl) }

        assertEquals(requestError.message, receivedError.message)
    }

    @Test
    fun get_succeedsOnResponseWithData() {
        val (sut, engine) = makeSut()
        val anyData = anyData
        engine.completeWithData(anyData)

        val response = sut.get(anyUrl)
        assertEquals(anyData, response.jsonString)
    }

    // region Helpers

    private fun makeSut(): Pair<HttpClient, MockEngine> {
        val engine = MockEngine { respond("") }
        val sut = KtorHttpClient(KtorClient(engine))

        return sut to engine
    }

    private val anyUrl = "http://any-url.com"
    private val anyData = "{[]}"

    private fun MockEngine.completeWithError(error: Exception) {
        config.requestHandlers.clear()
        config.requestHandlers.add { throw error }
    }

    private fun MockEngine.completeWithData(data: String) {
        config.requestHandlers.clear()
        config.requestHandlers.add { respond(data) }
    }

    // endregion
}
