package com.asturiancoder.photofeed.feed.cache

import com.asturiancoder.photofeed.feed.feature.FeedCache
import com.asturiancoder.photofeed.feed.feature.FeedLoader
import com.asturiancoder.photofeed.feed.feature.FeedPhoto

class LocalFeedRepository(
    private val store: FeedStore,
    private val currentTimestamp: () -> Long,
) : FeedLoader, FeedCache {

    private object InvalidCache : Exception()

    override fun load(): List<FeedPhoto> {
        val cache = store.retrieve()

        return if (cache != null && FeedCachePolicy.validate(cache.timestamp, currentTimestamp())) {
            cache.feed
        } else {
            emptyList()
        }
    }

    fun validateCache() {
        try {
            val cache = store.retrieve()

            if (cache != null && !FeedCachePolicy.validate(cache.timestamp, currentTimestamp())) {
                throw InvalidCache
            }
        } catch (exception: Exception) {
            store.deleteCachedFeed()
        }
    }

    override fun save(feed: List<FeedPhoto>) {
        store.deleteCachedFeed()
        store.insert(feed, currentTimestamp())
    }
}
