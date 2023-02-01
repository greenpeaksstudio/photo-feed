package com.asturiancoder.photofeed.feed.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

internal object FeedPhotosMapper {

    @Serializable
    private data class Root(
        @SerialName("photos") val photos: List<RemoteFeedPhoto>
    )

    private const val OK_200 = 200

    fun map(response: HttpResponse): List<RemoteFeedPhoto> {
        if (response.code == OK_200) {
            val root = Json.decodeFromString<Root>(response.jsonString)
            return root.photos
        }

        throw RemoteFeedLoader.Error.InvalidData
    }
}