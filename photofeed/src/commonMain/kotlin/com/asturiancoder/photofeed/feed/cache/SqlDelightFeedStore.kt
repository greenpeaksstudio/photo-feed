package com.asturiancoder.photofeed.feed.cache

import com.asturiancoder.photofeed.feed.cache.db.PhotoFeedDB
import com.asturiancoder.photofeed.feed.cache.model.CachedFeed
import com.asturiancoder.photofeed.feed.cache.model.LocalFeedPhoto
import com.asturiancoder.photofeed.feed.feature.FeedPhoto
import com.asturiancoder.photofeed.util.Uuid

class SqlDelightFeedStore(
    db: PhotoFeedDB,
) {

    private val queries = db.photoFeedDBQueries

    fun retrieve(): CachedFeed? {
        return queries.retrieve()
            .executeAsList()
            .map { localFeedCache ->
                CachedFeed(localFeedCache.feed.toModel(), localFeedCache.timestamp)
            }.firstOrNull()
    }

    fun insert(feed: List<FeedPhoto>, timestamp: Long) {
        queries.clear()
        queries.insert(feed.toLocal(), timestamp)
    }

    fun deleteCachedFeed() {
    }
}

private fun List<LocalFeedPhoto>.toModel(): List<FeedPhoto> = mapNotNull { localFeedPhoto ->
    Uuid.from(localFeedPhoto.id)?.let {
        FeedPhoto(
            id = it,
            description = localFeedPhoto.description,
            location = localFeedPhoto.location,
            url = localFeedPhoto.url,
            likes = localFeedPhoto.likes,
            author = FeedPhoto.Author(
                name = localFeedPhoto.authorName,
                imageUrl = localFeedPhoto.authorImageUrl,
            ),
        )
    }
}

private fun List<FeedPhoto>.toLocal(): List<LocalFeedPhoto> = map {
    LocalFeedPhoto(
        id = it.id.uuidString,
        description = it.description,
        location = it.location,
        url = it.url,
        likes = it.likes,
        authorName = it.author.name,
        authorImageUrl = it.author.imageUrl,
    )
}
