package com.greenpeaks.photofeed.feed.feature

import com.greenpeaks.photofeed.util.Uuid

data class FeedPhoto(
    val id: Uuid,
    val description: String?,
    val location: String?,
    val url: String,
    val likes: Int,
    val author: Author,
) {

    data class Author(
        val name: String,
        val imageUrl: String,
    )
}
