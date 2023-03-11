package com.asturiancoder.photofeed.feed.api

import com.asturiancoder.photofeed.feed.api.model.HttpResponse
import com.asturiancoder.photofeed.feed.api.model.HttpStatusCode
import com.asturiancoder.photofeed.feed.feature.FeedPhoto
import com.asturiancoder.photofeed.util.Uuid
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

internal object FeedPhotosMapper {

    @Serializable
    private data class Root(
        @SerialName("photos") val photos: List<RemoteFeedPhoto>,
    ) {
        @Serializable
        private data class RemoteFeedPhoto(
            @SerialName("id") val id: String,
            @SerialName("description") val description: String? = null,
            @SerialName("location") val location: String? = null,
            @SerialName("url") val url: String,
            @SerialName("likes") val likes: Int,
            @SerialName("author") val author: Author,
        ) {
            @Serializable
            data class Author(
                @SerialName("name") val name: String,
                @SerialName("image_url") val imageUrl: String,
            )
        }

        val feed: List<FeedPhoto>
            get() = photos.mapNotNull { remotePhoto ->
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
    }

    private object InvalidData : Exception()

    fun map(response: HttpResponse): List<FeedPhoto> {
        if (response.code != HttpStatusCode.OK) throw InvalidData

        try {
            val root = Json.decodeFromString<Root>(response.jsonString)
            return root.feed
        } catch (exception: SerializationException) {
            throw InvalidData
        }
    }
}
