package com.greenpeaks.photofeed.feed.cache

import com.greenpeaks.photofeed.feed.cache.db.PhotoFeedDB
import com.greenpeaks.photofeed.feed.cache.model.CachedFeed
import com.greenpeaks.photofeed.feed.cache.model.LocalFeedPhoto

class SqlDelightFeedStore(
    db: PhotoFeedDB,
) : FeedStore {

    private val queries = db.photoFeedDBQueries

    override fun retrieve(): CachedFeed? {
        return queries.retrieve()
            .executeAsList()
            .map { localFeedCache ->
                CachedFeed(localFeedCache.feed, localFeedCache.timestamp)
            }.firstOrNull()
    }

    override fun insert(feed: List<LocalFeedPhoto>, timestamp: Long) {
        queries.clear()
        queries.insert(feed, timestamp)
    }

    override fun deleteCachedFeed() {
        queries.clear()
    }
}
