package com.asturiancoder.photofeed

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RemoteFeedLoader(
    private val url: String,
    private val client: HttpClient,
) {

    fun load() {
        client.get(url = url)
    }
}

class HttpClient {
    var requestedUrl: String? = null

    fun get(url: String) {
        requestedUrl = url
    }
}

class RemoteFeedLoaderTests {

    @Test
    fun init_doesNotStartRequestFromUrl() {
        val url = "https://a-url.com"
        val client = HttpClient()
        RemoteFeedLoader(url, client)

        assertNull(client.requestedUrl)
    }

    @Test
    fun load_startsRequestFromUrl() {
        val url = "https://a-given-url.com"
        val client = HttpClient()
        val sut = RemoteFeedLoader(url, client)

        sut.load()

        assertEquals(url, client.requestedUrl)
    }
}