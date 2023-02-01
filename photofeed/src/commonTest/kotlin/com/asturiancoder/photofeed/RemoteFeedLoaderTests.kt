package com.asturiancoder.photofeed

import com.asturiancoder.photofeed.feed.api.HttpClient
import com.asturiancoder.photofeed.feed.api.HttpResponse
import com.asturiancoder.photofeed.feed.api.RemoteFeedLoader
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RemoteFeedLoaderTests {

    @Test
    fun init_doesNotStartRequestFromUrl() {
        val (_, client) = makeSut()

        assertTrue(client.requestedUrls.isEmpty())
    }

    @Test
    fun load_startsRequestFromUrl() {
        val url = "https://a-given-url.com"
        val (sut, client) = makeSut(url)

        sut.load()

        assertEquals(listOf(url), client.requestedUrls)
    }

    @Test
    fun load_twice_startsRequestFromUrlTwice() {
        val url = "https://a-given-url.com"
        val (sut, client) = makeSut(url)

        sut.load()
        sut.load()

        assertEquals(listOf(url, url), client.requestedUrls)
    }

    @Test
    fun load_deliversErrorOnClientError() {
        val (sut, client) = makeSut()
        val clientError = Exception()
        client.stubWith(Result.failure(clientError))

        val receivedResult = sut.load()

        assertEquals(Result.failure(RemoteFeedLoader.Error.Connectivity), receivedResult)
    }

    @Test
    fun load_deliversErrorOnNon200HttpResponse() {
        val (sut, client) = makeSut()

        val samples = listOf(199, 201, 300, 400, 500)
        samples.forEach { httpCode ->
            client.stubWith(Result.success(HttpResponse(httpCode)))
            val receivedResult = sut.load()

            assertEquals(Result.failure(RemoteFeedLoader.Error.InvalidData), receivedResult)
        }

    }

    // region Helpers

    private fun makeSut(url: String = "https://a-url.com"): Pair<RemoteFeedLoader, HttpClientStub> {
        val client = HttpClientStub()
        val sut = RemoteFeedLoader(url, client)

        return sut to client
    }

    private class HttpClientStub : HttpClient {
        val requestedUrls = mutableListOf<String>()

        private var stub = Result.success(HttpResponse(200))

        override fun get(url: String): Result<HttpResponse> {
            requestedUrls.add(url)
            return stub
        }

        fun stubWith(stub: Result<HttpResponse>) {
            this.stub = stub
        }
    }

    // endregion
}