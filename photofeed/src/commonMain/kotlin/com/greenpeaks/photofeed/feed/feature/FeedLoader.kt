package com.greenpeaks.photofeed.feed.feature

interface FeedLoader {
    fun load(): List<FeedPhoto>
}
