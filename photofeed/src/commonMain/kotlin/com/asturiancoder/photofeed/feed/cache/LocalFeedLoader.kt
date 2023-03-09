package com.asturiancoder.photofeed.feed.cache

import com.asturiancoder.photofeed.feed.feature.FeedLoader
import com.asturiancoder.photofeed.feed.feature.FeedPhoto
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus

class LocalFeedLoader(
    private val store: FeedStore,
    private val currentTimestamp: () -> Long,
) : FeedLoader {

    private val maxCacheAgeInDays = 5

    override fun load(): Result<List<FeedPhoto>> {
        return try {
            val cache = store.retrieve()

            if (cache != null && validate(cache.timestamp, currentTimestamp())) {
                Result.success(cache.feed)
            } else {
                Result.success(emptyList())
            }
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    private fun validate(cacheTimestamp: Long, currentTimestamp: Long): Boolean {
        val maxCacheAge = Instant.fromEpochSeconds(cacheTimestamp)
            .plus(maxCacheAgeInDays, DateTimeUnit.DAY, TimeZone.UTC)
        return currentTimestamp < maxCacheAge.epochSeconds
    }
}
