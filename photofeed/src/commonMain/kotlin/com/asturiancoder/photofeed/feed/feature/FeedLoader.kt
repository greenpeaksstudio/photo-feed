package com.asturiancoder.photofeed.feed.feature

interface FeedLoader {
    fun load(): List<FeedPhoto>
}
