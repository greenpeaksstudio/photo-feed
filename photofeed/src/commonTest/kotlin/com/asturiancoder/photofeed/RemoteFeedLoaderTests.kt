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

    // region Helpers

    private fun makeSut(url: String = "https://a-url.com"): Pair<RemoteFeedLoader, HttpClientSpy> {
        val client = HttpClientSpy()
        val sut = RemoteFeedLoader(url, client)

        return sut to client
    }

    private class HttpClientSpy : HttpClient {
        var requestedUrls = mutableListOf<String>()

        override fun get(url: String) {
            requestedUrls.add(url)
        }
    }

    // endregion
}