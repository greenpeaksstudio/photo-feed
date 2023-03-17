package com.asturiancoder.photofeed.feed.cache

import com.asturiancoder.photofeed.feed.cache.model.LocalFeedPhoto
import com.asturiancoder.photofeed.feed.feature.FeedCache
import com.asturiancoder.photofeed.feed.feature.FeedLoader
import com.asturiancoder.photofeed.feed.feature.FeedPhoto
import com.asturiancoder.photofeed.util.Uuid

class LocalFeedRepository(
    private val store: FeedStore,
    private val currentTimestamp: () -> Long,
) : FeedLoader, FeedCache {

    private object InvalidCache : Exception()

    override fun load(): List<FeedPhoto> {
        val cache = store.retrieve()

        return if (cache != null && FeedCachePolicy.validate(cache.timestamp, currentTimestamp())) {
            cache.feed.toModel()
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
        store.insert(feed.toLocal(), currentTimestamp())
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
