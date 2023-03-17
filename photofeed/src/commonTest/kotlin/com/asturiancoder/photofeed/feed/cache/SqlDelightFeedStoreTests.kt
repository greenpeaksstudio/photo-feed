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
import kotlin.test.fail

class SqlDelightFeedStoreTests {

    @Test
    fun retrieve_deliversEmptyOnEmptyCache() {
        val sut = makeSut()

        val receivedCache = sut.retrieve()

        assertNull(receivedCache)
    }

    @Test
    fun retrieve_hasNoSideEffectsOnEmptyCache() {
        val sut = makeSut()

        val firstReceivedCache = sut.retrieve()
        val lastReceivedCache = sut.retrieve()

        assertNull(firstReceivedCache)
        assertNull(lastReceivedCache)
    }

    @Test
    fun retrieve_deliversFoundValuesOnNonEmptyCache() {
        val sut = makeSut()
        val feed = uniquePhotoFeed()
        val timestamp = Clock.System.now().epochSeconds
        sut.insert(feed, timestamp)

        val receivedCache = sut.retrieve()

        assertEquals(CachedFeed(feed, timestamp), receivedCache)
    }

    @Test
    fun retrieve_hasNoSideEffectsOnNonEmptyCache() {
        val sut = makeSut()
        val feed = uniquePhotoFeed()
        val timestamp = Clock.System.now().epochSeconds
        sut.insert(feed, timestamp)

        val firstReceivedCache = sut.retrieve()
        val lastReceivedCache = sut.retrieve()

        val expectedCache = CachedFeed(feed, timestamp)
        assertEquals(expectedCache, firstReceivedCache)
        assertEquals(expectedCache, lastReceivedCache)
    }

    @Test
    fun insert_deliversNoErrorOnEmptyCache() {
        val sut = makeSut()
        val feed = uniquePhotoFeed()
        val timestamp = Clock.System.now().epochSeconds

        try {
            sut.insert(feed, timestamp)
        } catch (exception: Exception) {
            fail("Expected to insert without error, got $exception instead")
        }
    }

    @Test
    fun insert_deliversNoErrorOnNonEmptyCache() {
        val sut = makeSut()
        val feed = uniquePhotoFeed()
        val timestamp = Clock.System.now().epochSeconds
        sut.insert(feed, timestamp)

        try {
            sut.insert(feed, timestamp)
        } catch (exception: Exception) {
            fail("Expected to insert without error, got $exception instead")
        }
    }

    @Test
    fun insert_overridesPreviouslyInsertedCacheValues() {
        val sut = makeSut()
        sut.insert(uniquePhotoFeed(), Clock.System.now().epochSeconds)

        val latestFeed = uniquePhotoFeed()
        val latestTimestamp = Clock.System.now().epochSeconds
        sut.insert(latestFeed, latestTimestamp)

        val receivedCache = sut.retrieve()
        assertEquals(CachedFeed(latestFeed, latestTimestamp), receivedCache)
    }

    @Test
    fun delete_deliversNoErrorOnEmptyCache() {
        val sut = makeSut()

        try {
            sut.deleteCachedFeed()
        } catch (exception: Exception) {
            fail("Expected to delete without error, got $exception instead")
        }
    }

    @Test
    fun delete_hasNoSideEffectsOnEmptyCache() {
        val sut = makeSut()

        sut.deleteCachedFeed()

        val receivedCache = sut.retrieve()
        assertNull(receivedCache)
    }

    @Test
    fun delete_deliversNoErrorOnNonEmptyCache() {
        val sut = makeSut()
        val feed = uniquePhotoFeed()
        val timestamp = Clock.System.now().epochSeconds
        sut.insert(feed, timestamp)

        try {
            sut.deleteCachedFeed()
        } catch (exception: Exception) {
            fail("Expected to delete without error, got $exception instead")
        }
    }

    @Test
    fun delete_emptiesPreviouslyInsertedCache() {
        val sut = makeSut()
        val feed = uniquePhotoFeed()
        val timestamp = Clock.System.now().epochSeconds
        sut.insert(feed, timestamp)

        sut.deleteCachedFeed()

        val receivedCache = sut.retrieve()
        assertNull(receivedCache)
    }

    // region Helpers

    private fun makeSut(): FeedStore {
        val adapter = LocalFeedCache.Adapter(FeedColumnAdapter())
        val db = PhotoFeedDB(TestSqlDelightDriverFactory.create(), adapter)

        return SqlDelightFeedStore(db)
    }

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
