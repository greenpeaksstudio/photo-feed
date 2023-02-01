package com.asturiancoder.photofeed

import com.asturiancoder.photofeed.feed.api.HttpClient
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
        val clientError = Exception()
        val client = HttpClientStub(Result.failure(clientError))
        val (sut, _) = makeSut(client = client)

        val receivedResult = sut.load()

        assertEquals(Result.failure(RemoteFeedLoader.Error.Connectivity), receivedResult)
    }

    // region Helpers

    private fun makeSut(url: String = "https://a-url.com", client: HttpClientStub = HttpClientStub()): Pair<RemoteFeedLoader, HttpClientStub> {
        val sut = RemoteFeedLoader(url, client)

        return sut to client
    }

    private class HttpClientStub(
        private val stub: Result<Unit> = Result.success(Unit)
    ) : HttpClient {
        var requestedUrls = mutableListOf<String>()

        override fun get(url: String): Result<Unit> {
            requestedUrls.add(url)
            return stub
        }
    }

    // endregion
}