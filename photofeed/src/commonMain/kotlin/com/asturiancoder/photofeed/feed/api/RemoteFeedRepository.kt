package com.asturiancoder.photofeed.feed.api

import com.asturiancoder.photofeed.feed.feature.FeedLoader
import com.asturiancoder.photofeed.feed.feature.FeedPhoto

class RemoteFeedRepository(
    private val url: String,
    private val client: HttpClient,
) : FeedLoader {

    sealed class Error : Exception() {
        object Connectivity : Error()
        object InvalidData : Error()
    }

    override fun load(): List<FeedPhoto> {
        val response = try {
            client.get(url = url)
        } catch (exception: Exception) {
            throw Error.Connectivity
        }

        return try {
            FeedPhotosMapper.map(response)
        } catch (exception: Exception) {
            throw Error.InvalidData
        }
    }
}
