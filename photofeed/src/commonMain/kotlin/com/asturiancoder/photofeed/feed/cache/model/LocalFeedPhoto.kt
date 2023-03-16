package com.asturiancoder.photofeed.feed.cache.model

data class LocalFeedPhoto(
    val id: String,
    val description: String?,
    val location: String?,
    val url: String,
    val likes: Int,
    val authorName: String,
    val authorImageUrl: String,
)
