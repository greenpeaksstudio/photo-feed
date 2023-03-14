package com.asturiancoder.photofeed.feed.feature

interface FeedCache {
    fun save(feed: List<FeedPhoto>)
}
