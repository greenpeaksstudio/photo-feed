package com.asturiancoder.photofeed.feed.feature

import com.asturiancoder.photofeed.util.Uuid

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
