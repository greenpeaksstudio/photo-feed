package com.asturiancoder.photofeed.feed.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class RemoteFeedPhoto(
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