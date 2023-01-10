package com.asturiancoder.photofeed

typealias LoadFeedResult = Result<List<FeedPhoto>>

interface FeedLoader {
    fun load(): LoadFeedResult
}