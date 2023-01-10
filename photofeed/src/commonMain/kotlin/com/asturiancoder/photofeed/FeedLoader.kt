package com.asturiancoder.photofeed

interface FeedLoader {
    fun load(): Result<List<FeedPhoto>>
}