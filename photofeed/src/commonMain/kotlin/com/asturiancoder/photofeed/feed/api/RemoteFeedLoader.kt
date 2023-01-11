package com.asturiancoder.photofeed.feed.api

internal interface HttpClient {
    fun get(url: String)
}

internal class RemoteFeedLoader(
    private val url: String,
    private val client: HttpClient,
) {

    fun load() {
        client.get(url = url)
    }
}