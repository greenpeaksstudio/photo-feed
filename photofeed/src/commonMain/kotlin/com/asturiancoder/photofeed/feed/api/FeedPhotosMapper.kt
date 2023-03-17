package com.asturiancoder.photofeed.feed.api

import com.asturiancoder.photofeed.feed.api.model.HttpResponse
import com.asturiancoder.photofeed.feed.api.model.HttpStatusCode
import com.asturiancoder.photofeed.feed.api.model.RemoteFeedPhoto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

internal object FeedPhotosMapper {

    @Serializable
    private data class Root(
        @SerialName("photos") val photos: List<RemoteFeedPhoto>,
    )

    private object InvalidData : Exception()

    fun map(response: HttpResponse): List<RemoteFeedPhoto> {
        if (response.code != HttpStatusCode.OK) throw InvalidData

        try {
            val root = Json.decodeFromString<Root>(response.jsonString)
            return root.photos
        } catch (exception: SerializationException) {
            throw InvalidData
        }
    }
}
