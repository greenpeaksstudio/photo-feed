package com.asturiancoder.photofeed.feed.api

import com.asturiancoder.photofeed.feed.feature.FeedLoader
import com.asturiancoder.photofeed.feed.feature.FeedPhoto

class RemoteFeedLoader(
    private val url: String,
    private val client: HttpClient,
) : FeedLoader {

    sealed class Error : Exception() {
        object Connectivity : Error()
        object InvalidData : Error()
    }

    override fun load(): Result<List<FeedPhoto>> {
        val result = client.get(url = url)

        return result
            .fold(
                onSuccess = { response ->
                    map(response)
                }, onFailure = {
                    Result.failure(Error.Connectivity)
                }
            )
    }

    private fun map(response: HttpResponse): Result<List<FeedPhoto>> {
        return try {
            Result.success(FeedPhotosMapper.map(response))
        } catch (e: Exception) {
            Result.failure(Error.InvalidData)
        }
    }
}