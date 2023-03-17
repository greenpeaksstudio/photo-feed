package com.asturiancoder.photofeed.feed.cache.model

data class CachedFeed(
    val feed: List<LocalFeedPhoto>,
    val timestamp: Long,
)
