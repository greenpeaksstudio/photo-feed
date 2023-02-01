package com.asturiancoder.photofeed.feed.api

import com.asturiancoder.photofeed.feed.feature.FeedPhoto

data class HttpResponse(
    val code: Int,
    val jsonString: String,
)

internal interface HttpClient {
    fun get(url: String): Result<HttpResponse>
}

internal class RemoteFeedLoader(
    private val url: String,
    private val client: HttpClient,
) {

    sealed class Error : Exception() {
        object Connectivity : Error()
        object InvalidData : Error()
    }

    fun load(): Result<List<FeedPhoto>> {
        val result = client.get(url = url)

        result.onSuccess { response ->
            if (response.code != 200) {
                return Result.failure(Error.InvalidData)
            }
            return Result.failure(Error.InvalidData)
        }
        return Result.failure(Error.Connectivity)
    }
}