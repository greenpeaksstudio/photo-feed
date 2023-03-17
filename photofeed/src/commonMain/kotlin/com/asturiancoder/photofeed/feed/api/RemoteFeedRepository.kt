package com.asturiancoder.photofeed.feed.api

import com.asturiancoder.photofeed.feed.api.model.RemoteFeedPhoto
import com.asturiancoder.photofeed.feed.feature.FeedLoader
import com.asturiancoder.photofeed.feed.feature.FeedPhoto
import com.asturiancoder.photofeed.util.Uuid

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
            FeedPhotosMapper.map(response).toModel()
        } catch (exception: Exception) {
            throw Error.InvalidData
        }
    }
}

private fun List<RemoteFeedPhoto>.toModel(): List<FeedPhoto> =
    mapNotNull { remotePhoto ->
        Uuid.from(remotePhoto.id)?.let { uuid ->
            FeedPhoto(
                id = uuid,
                description = remotePhoto.description,
                location = remotePhoto.location,
                url = remotePhoto.url,
                likes = remotePhoto.likes,
                author = FeedPhoto.Author(
                    name = remotePhoto.author.name,
                    imageUrl = remotePhoto.author.imageUrl,
                ),
            )
        }
    }
