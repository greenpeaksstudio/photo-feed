package com.asturiancoder.photofeed.feed.cache

import com.asturiancoder.photofeed.feed.feature.FeedLoader
import com.asturiancoder.photofeed.feed.feature.FeedPhoto

class LocalFeedRepository(
    private val store: FeedStore,
    private val currentTimestamp: () -> Long,
) : FeedLoader {

    override fun load(): Result<List<FeedPhoto>> {
        return try {
            val cache = store.retrieve()

            if (cache != null && FeedCachePolicy.validate(cache.timestamp, currentTimestamp())) {
                Result.success(cache.feed)
            } else {
                Result.success(emptyList())
            }
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    fun validateCache() {
        try {
            store.retrieve()
        } catch (exception: Exception) {
            store.deleteCachedFeed()
        }
    }
}
