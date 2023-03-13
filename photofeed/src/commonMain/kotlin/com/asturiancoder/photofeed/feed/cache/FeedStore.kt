package com.asturiancoder.photofeed.feed.cache

import com.asturiancoder.photofeed.feed.cache.model.CachedFeed

interface FeedStore {
    fun retrieve(): CachedFeed?
    fun deleteCachedFeed()
}
