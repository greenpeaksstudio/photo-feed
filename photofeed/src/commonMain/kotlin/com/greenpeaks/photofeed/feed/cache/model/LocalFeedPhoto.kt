package com.greenpeaks.photofeed.feed.cache.model

import kotlinx.serialization.Serializable

@Serializable
data class LocalFeedPhoto(
    val id: String,
    val description: String?,
    val location: String?,
    val url: String,
    val likes: Int,
    val authorName: String,
    val authorImageUrl: String,
)
