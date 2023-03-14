package com.asturiancoder.photofeed.feed.cache

import com.asturiancoder.photofeed.feed.cache.model.CachedFeed
import com.asturiancoder.photofeed.feed.feature.FeedPhoto

interface FeedStore {
    fun retrieve(): CachedFeed?
    fun deleteCachedFeed()
    fun insert(feed: List<FeedPhoto>, timestamp: Long)
}
