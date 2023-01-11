package com.asturiancoder.photofeed.feed.feature

data class FeedPhoto(
    val id: String,
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
