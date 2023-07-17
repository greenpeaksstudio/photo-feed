package com.greenpeaks.photofeed.feed.cache

import com.greenpeaks.photofeed.feed.cache.model.CachedFeed
import com.greenpeaks.photofeed.feed.cache.model.LocalFeedPhoto

interface FeedStore {
    fun retrieve(): CachedFeed?
    fun deleteCachedFeed()
    fun insert(feed: List<LocalFeedPhoto>, timestamp: Long)
}
