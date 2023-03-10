package com.asturiancoder.photofeed.feed.api

import com.asturiancoder.photofeed.feed.api.model.HttpResponse
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
        return try {
            map(client.get(url = url))
        } catch (exception: Exception) {
            Result.failure(Error.Connectivity)
        }
    }

    private fun map(response: HttpResponse): Result<List<FeedPhoto>> {
        return try {
            Result.success(FeedPhotosMapper.map(response))
        } catch (exception: Error.InvalidData) {
            Result.failure(exception)
        }
    }
}
