package com.greenpeaks.photofeed.feed.feature

interface FeedCache {
    fun save(feed: List<FeedPhoto>)
}
