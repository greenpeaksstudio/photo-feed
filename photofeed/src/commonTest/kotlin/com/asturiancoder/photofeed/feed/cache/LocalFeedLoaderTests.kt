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
import kotlinx.datetime.plus
import kotlin.test.Test
import kotlin.test.assertEquals

internal class LocalFeedLoader(
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

        expect(sut, expectedResult = Result.failure(retrievalError)) {
            store.completeRetrievalWithError(retrievalError)
        }
    }

    @Test
    fun load_deliversNoPhotosOnEmptyCache() {
        val (sut, store) = makeSut()

        expect(sut, expectedResult = Result.success(emptyList())) {
            store.completeRetrievalWithEmptyCache()
        }
    }

    @Test
    fun load_deliversCachedPhotosOnNonExpiredCache() {
        val feed = uniquePhotoFeed()
        val fixedCurrentTimestamp = Clock.System.now()
        val nonExpiredTimestamp = fixedCurrentTimestamp.minusFeedCacheMaxAge().adding(seconds = 1)
        val (sut, store) = makeSut(currentTimestamp = { fixedCurrentTimestamp })

        expect(sut, expectedResult = Result.success(feed)) {
            store.completeRetrievalWith(feed = feed, timestamp = nonExpiredTimestamp)
        }
    }

    @Test
    fun load_deliversNoPhotosOnCacheExpiration() {
        val feed = uniquePhotoFeed()
        val fixedCurrentTimestamp = Clock.System.now()
        val expirationTimestamp = fixedCurrentTimestamp.minusFeedCacheMaxAge()
        val (sut, store) = makeSut(currentTimestamp = { fixedCurrentTimestamp })

        expect(sut, expectedResult = Result.success(emptyList())) {
            store.completeRetrievalWith(feed = feed, timestamp = expirationTimestamp)
        }
    }

    @Test
    fun load_deliversNoPhotosOnExpiredCache() {
        val feed = uniquePhotoFeed()
        val fixedCurrentTimestamp = Clock.System.now()
        val expiredTimestamp = fixedCurrentTimestamp.minusFeedCacheMaxAge().adding(seconds = -1)
        val (sut, store) = makeSut(currentTimestamp = { fixedCurrentTimestamp })

        expect(sut, expectedResult = Result.success(emptyList())) {
            store.completeRetrievalWith(feed = feed, timestamp = expiredTimestamp)
        }
    }

    // region Helpers

    private fun makeSut(
        currentTimestamp: () -> Instant = Clock.System::now,
    ): Pair<LocalFeedLoader, FeedStoreSpy> {
        val store = FeedStoreSpy()
        val sut = LocalFeedLoader(store, currentTimestamp = { currentTimestamp().epochSeconds })

        return sut to store
    }

    private fun expect(
        sut: LocalFeedLoader,
        expectedResult: Result<List<FeedPhoto>>,
        action: () -> Unit,
    ) {
        action()

        val receivedResult = sut.load()

        assertEquals(
            expectedResult,
            receivedResult,
            "Expected result $expectedResult, got $receivedResult instead",
        )
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

    private val feedCacheMaxAgeInDays = 5

    private fun Instant.minusFeedCacheMaxAge(): Instant =
        this.minus(feedCacheMaxAgeInDays, DateTimeUnit.DAY, TimeZone.UTC)

    private fun Instant.adding(seconds: Int): Instant =
        this.plus(seconds, DateTimeUnit.SECOND)

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
