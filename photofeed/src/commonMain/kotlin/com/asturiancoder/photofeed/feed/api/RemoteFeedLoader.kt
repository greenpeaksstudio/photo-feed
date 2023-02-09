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
            val remotePhotos = FeedPhotosMapper.map(response)
            Result.success(remotePhotos.toModels())
        } catch (e: Exception) {
            Result.failure(Error.InvalidData)
        }
    }
}

private fun List<RemoteFeedPhoto>.toModels(): List<FeedPhoto> {
    return map { remotePhoto ->
        FeedPhoto(
            id = remotePhoto.id,
            description = remotePhoto.description,
            location = remotePhoto.location,
            url = remotePhoto.url,
            likes = remotePhoto.likes,
            author = FeedPhoto.Author(
                name = remotePhoto.author.name,
                imageUrl = remotePhoto.author.imageUrl
            )
        )
    }
}