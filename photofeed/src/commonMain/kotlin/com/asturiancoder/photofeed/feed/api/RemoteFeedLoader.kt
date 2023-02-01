package com.asturiancoder.photofeed.feed.api

import com.asturiancoder.photofeed.feed.feature.FeedPhoto

internal data class HttpResponse(
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

        return result
            .fold(
                onSuccess = { response ->
                    try {
                        FeedPhotosMapper.map(response)
                        Result.success(listOf())
                    } catch (e: Exception) {
                        Result.failure(Error.InvalidData)
                    }
                }, onFailure = {
                    Result.failure(Error.Connectivity)
                }
            )
    }
}