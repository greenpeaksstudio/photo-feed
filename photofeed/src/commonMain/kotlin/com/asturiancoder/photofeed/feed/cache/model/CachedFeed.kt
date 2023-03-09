package com.asturiancoder.photofeed.feed.cache.model

import com.asturiancoder.photofeed.feed.feature.FeedPhoto

data class CachedFeed(
    val feed: List<FeedPhoto>,
    val timestamp: Long,
)
