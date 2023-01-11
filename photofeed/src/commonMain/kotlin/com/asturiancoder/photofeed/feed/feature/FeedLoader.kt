package com.asturiancoder.photofeed.feed.feature

typealias LoadFeedResult = Result<List<FeedPhoto>>

interface FeedLoader {
    fun load(): LoadFeedResult
}