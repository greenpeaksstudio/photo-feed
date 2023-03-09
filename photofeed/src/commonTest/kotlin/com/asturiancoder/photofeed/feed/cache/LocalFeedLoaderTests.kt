package com.asturiancoder.photofeed.feed.cache

import com.asturiancoder.photofeed.feed.cache.LocalFeedLoaderTests.FeedStoreSpy.Message.RETRIEVE
import com.asturiancoder.photofeed.feed.feature.FeedLoader
import com.asturiancoder.photofeed.feed.feature.FeedPhoto
import com.asturiancoder.photofeed.util.Uuid
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlin.test.Test
import kotlin.test.assertEquals

internal class LocalFeedLoader(
    private val store: FeedStore,
) : FeedLoader {
    override fun load(): Result<List<FeedPhoto>> {
        return try {
            Result.success(store.retrieve()?.feed ?: emptyList())
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
}

data class CachedFeed(
    val feed: List<FeedPhoto>,
    val timestamp: Long,
)

internal interface FeedStore {
    fun retrieve(): CachedFeed?
}

class LocalFeedLoaderTests {

    @Test
    fun init_doesNotMessageStoreUponCreation() {
        val (_, store) = makeSut()

        assertEquals(0, store.receivedMessages.count())
    }

    @Test
    fun load_requestsCacheRetrieval() {
        val (sut, store) = makeSut()

        sut.load()

        assertEquals(listOf(RETRIEVE), store.receivedMessages)
    }

    @Test
    fun load_failsOnRetrievalError() {
        val (sut, store) = makeSut()
        val retrievalError = Exception()

        store.completeRetrievalWithError(retrievalError)
        val receivedResult = sut.load()

        assertEquals(Result.failure(retrievalError), receivedResult)
    }

    @Test
    fun load_deliversNoPhotosOnEmptyCache() {
        val (sut, store) = makeSut()

        store.completeRetrievalWithEmptyCache()
        val receivedResult = sut.load()

        assertEquals(Result.success(emptyList()), receivedResult)
    }

    @Test
    fun load_deliversCachedPhotosOnNonExpiredCache() {
        val feed = uniquePhotoFeed()
        val fixedCurrentTimestamp = Clock.System.now()
        val nonExpiredTimestamp = fixedCurrentTimestamp.minusFeedCacheMaxAge().adding(seconds = 1)
        val (sut, store) = makeSut()

        store.completeRetrievalWith(feed = feed, timestamp = nonExpiredTimestamp)
        val receivedResult = sut.load()

        assertEquals(Result.success(feed), receivedResult)
    }

    // region Helpers

    private fun makeSut(): Pair<LocalFeedLoader, FeedStoreSpy> {
        val store = FeedStoreSpy()
        val sut = LocalFeedLoader(store)

        return sut to store
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

    private fun Instant.minusFeedCacheMaxAge(): Instant =
        this.minus(feedCacheMaxAgeInDays, DateTimeUnit.DAY, TimeZone.currentSystemDefault())

    private val Instant.feedCacheMaxAgeInDays: Int
        get() = 5

    private fun Instant.adding(seconds: Int): Instant =
        this.minus(seconds, DateTimeUnit.SECOND)

    class FeedStoreSpy : FeedStore {
        enum class Message {
            RETRIEVE,
        }

        val receivedMessages = mutableListOf<Message>()

        private var retrievalResult: Result<CachedFeed?>? = null

        override fun retrieve(): CachedFeed? {
            receivedMessages.add(RETRIEVE)
            return retrievalResult?.getOrThrow()
        }

        fun completeRetrievalWithError(error: Exception) {
            retrievalResult = Result.failure(error)
        }

        fun completeRetrievalWithEmptyCache() {
            retrievalResult = Result.success(null)
        }

        fun completeRetrievalWith(feed: List<FeedPhoto>, timestamp: Instant) {
            retrievalResult = Result.success(CachedFeed(feed, timestamp.epochSeconds))
        }
    }

    // endregion
}
