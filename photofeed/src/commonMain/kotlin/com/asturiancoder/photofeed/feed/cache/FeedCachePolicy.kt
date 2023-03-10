package com.asturiancoder.photofeed.feed.cache

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus

object FeedCachePolicy {

    private const val MAX_CACHE_AGE_IN_DAYS = 5

    fun validate(cacheTimestamp: Long, currentTimestamp: Long): Boolean {
        val maxCacheAge = Instant.fromEpochSeconds(cacheTimestamp)
            .plus(MAX_CACHE_AGE_IN_DAYS, DateTimeUnit.DAY, TimeZone.UTC)
        return currentTimestamp < maxCacheAge.epochSeconds
    }
}
