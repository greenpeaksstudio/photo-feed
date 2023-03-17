package com.asturiancoder.photofeed.feed.cache

import com.asturiancoder.photofeed.feed.cache.db.FeedColumnAdapter
import com.asturiancoder.photofeed.feed.cache.db.LocalFeedCache
import com.asturiancoder.photofeed.feed.cache.db.PhotoFeedDB
import com.asturiancoder.photofeed.feed.cache.db.TestSqlDelightDriverFactory
import com.asturiancoder.photofeed.feed.cache.model.CachedFeed
import com.asturiancoder.photofeed.feed.feature.FeedPhoto
import com.asturiancoder.photofeed.util.Uuid
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SqlDelightFeedStoreTests {

    @Test
    fun retrieve_deliversEmptyOnEmptyCache() {
        val adapter = LocalFeedCache.Adapter(FeedColumnAdapter())
        val db = PhotoFeedDB(TestSqlDelightDriverFactory.create(), adapter)
        val sut = SqlDelightFeedStore(db)

        val receivedCache = sut.retrieve()
        assertNull(receivedCache)
    }

    @Test
    fun retrieve_hasNoSideEffectsOnEmptyCache() {
        val adapter = LocalFeedCache.Adapter(FeedColumnAdapter())
        val db = PhotoFeedDB(TestSqlDelightDriverFactory.create(), adapter)
        val sut = SqlDelightFeedStore(db)

        var receivedCache = sut.retrieve()
        assertNull(receivedCache)

        receivedCache = sut.retrieve()
        assertNull(receivedCache)
    }

    @Test
    fun retrieve_deliversFoundValuesOnNonEmptyCache() {
        val feed = uniquePhotoFeed()
        val timestamp = Clock.System.now().epochSeconds
        val adapter = LocalFeedCache.Adapter(FeedColumnAdapter())
        val db = PhotoFeedDB(TestSqlDelightDriverFactory.create(), adapter)
        val sut = SqlDelightFeedStore(db)

        sut.insert(feed, timestamp)

        val receivedCache = sut.retrieve()

        assertEquals(CachedFeed(feed, timestamp), receivedCache)
    }

    // region Helpers

    private fun uniquePhotoFeed(): List<FeedPhoto> =
        listOf(uniquePhoto(), uniquePhoto())

    private fun uniquePhoto() = FeedPhoto(
        id = Uuid(),
        description = "A description",
        location = "A location",
        url = "http://a-url.com",
        likes = 0,
        author = FeedPhoto.Author(name = "An author", imageUrl = "https://an-author-url.com"),
    )

    // endregion
}
