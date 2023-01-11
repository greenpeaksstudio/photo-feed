package com.asturiancoder.photofeed

import kotlin.test.Test
import kotlin.test.assertNull

class RemoteFeedLoader {

}

class HttpClient {
    var requestedUrl: String? = null

}

class RemoteFeedLoaderTests {

    @Test
    fun init_doesNotStartRequestFromUrl() {
        val client = HttpClient()
        RemoteFeedLoader()

        assertNull(client.requestedUrl)
    }
}