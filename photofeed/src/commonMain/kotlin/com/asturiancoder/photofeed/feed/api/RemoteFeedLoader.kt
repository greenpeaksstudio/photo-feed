package com.asturiancoder.photofeed.feed.api

import com.asturiancoder.photofeed.feed.feature.FeedPhoto

internal interface HttpClient {
    fun get(url: String): Result<Unit>
}

internal class RemoteFeedLoader(
    private val url: String,
    private val client: HttpClient,
) {

    sealed class Error : Exception() {
        object Connectivity: Error()
    }

    fun load() : Result<List<FeedPhoto>>{
        client.get(url = url)
        return Result.failure(Error.Connectivity)
    }
}