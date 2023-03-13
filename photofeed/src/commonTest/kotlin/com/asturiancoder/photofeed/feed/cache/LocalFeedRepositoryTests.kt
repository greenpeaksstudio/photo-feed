package com.asturiancoder.photofeed.feed.cache

import com.asturiancoder.photofeed.feed.cache.LocalFeedRepositoryTests.FeedStoreSpy.Message.DELETE_CACHED_FEED
import com.asturiancoder.photofeed.feed.cache.LocalFeedRepositoryTests.FeedStoreSpy.Message.RETRIEVE
import com.asturiancoder.photofeed.feed.cache.model.CachedFeed
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

class LocalFeedRepositoryTests {

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

        sut.expectLoad(expectedResult = Result.failure(retrievalError)) {
            store.completeRetrievalWithError(retrievalError)
        }
    }

    @Test
    fun load_deliversNoPhotosOnEmptyCache() {
        val (sut, store) = makeSut()

        sut.expectLoad(expectedResult = Result.success(emptyList())) {
            store.completeRetrievalWithEmptyCache()
        }
    }

    @Test
    fun load_deliversCachedPhotosOnNonExpiredCache() {
        val feed = uniquePhotoFeed()
        val fixedCurrentTimestamp = Clock.System.now()
        val nonExpiredTimestamp = fixedCurrentTimestamp.minusFeedCacheMaxAge().adding(seconds = 1)
        val (sut, store) = makeSut(currentTimestamp = { fixedCurrentTimestamp })

        sut.expectLoad(expectedResult = Result.success(feed)) {
            store.completeRetrievalWith(feed = feed, timestamp = nonExpiredTimestamp)
        }
    }

    @Test
    fun load_deliversNoPhotosOnCacheExpiration() {
        val feed = uniquePhotoFeed()
        val fixedCurrentTimestamp = Clock.System.now()
        val expirationTimestamp = fixedCurrentTimestamp.minusFeedCacheMaxAge()
        val (sut, store) = makeSut(currentTimestamp = { fixedCurrentTimestamp })

        sut.expectLoad(expectedResult = Result.success(emptyList())) {
            store.completeRetrievalWith(feed = feed, timestamp = expirationTimestamp)
        }
    }

    @Test
    fun load_deliversNoPhotosOnExpiredCache() {
        val feed = uniquePhotoFeed()
        val fixedCurrentTimestamp = Clock.System.now()
        val expiredTimestamp = fixedCurrentTimestamp.minusFeedCacheMaxAge().adding(seconds = -1)
        val (sut, store) = makeSut(currentTimestamp = { fixedCurrentTimestamp })

        sut.expectLoad(expectedResult = Result.success(emptyList())) {
            store.completeRetrievalWith(feed = feed, timestamp = expiredTimestamp)
        }
    }

    @Test
    fun validateCache_deletesCacheOnRetrievalError() {
        val (sut, store) = makeSut()
        val retrievalError = Exception()
        store.completeRetrievalWithError(retrievalError)

        sut.validateCache()

        assertEquals(listOf(RETRIEVE, DELETE_CACHED_FEED), store.receivedMessages)
    }

    @Test
    fun validateCache_doesNotDeleteCacheOnEmptyCache() {
        val (sut, store) = makeSut()
        store.completeRetrievalWithEmptyCache()

        sut.validateCache()

        assertEquals(listOf(RETRIEVE), store.receivedMessages)
    }

    @Test
    fun validateCache_doesNotDeleteCacheOnNonExpiredCache() {
        val feed = uniquePhotoFeed()
        val fixedCurrentTimestamp = Clock.System.now()
        val nonExpiredTimestamp = fixedCurrentTimestamp.minusFeedCacheMaxAge().adding(seconds = 1)
        val (sut, store) = makeSut(currentTimestamp = { fixedCurrentTimestamp })
        store.completeRetrievalWith(feed, nonExpiredTimestamp)

        sut.validateCache()

        assertEquals(listOf(RETRIEVE), store.receivedMessages)
    }

    @Test
    fun validateCache_deletesCacheOnExpiration() {
        val feed = uniquePhotoFeed()
        val fixedCurrentTimestamp = Clock.System.now()
        val expirationTimestamp = fixedCurrentTimestamp.minusFeedCacheMaxAge()
        val (sut, store) = makeSut(currentTimestamp = { fixedCurrentTimestamp })
        store.completeRetrievalWith(feed, expirationTimestamp)

        sut.validateCache()

        assertEquals(listOf(RETRIEVE, DELETE_CACHED_FEED), store.receivedMessages)
    }

    @Test
    fun validateCache_deletesCacheOnExpiredCache() {
        val feed = uniquePhotoFeed()
        val fixedCurrentTimestamp = Clock.System.now()
        val expiredTimestamp = fixedCurrentTimestamp.minusFeedCacheMaxAge().adding(seconds = -1)
        val (sut, store) = makeSut(currentTimestamp = { fixedCurrentTimestamp })
        store.completeRetrievalWith(feed, expiredTimestamp)

        sut.validateCache()

        assertEquals(listOf(RETRIEVE, DELETE_CACHED_FEED), store.receivedMessages)
    }

    @Test
    fun validateCache_failsOnDeletionErrorAfterFailedRetrieval() {
        val (sut, store) = makeSut()
        val deletionError = Exception()
        store.completeRetrievalWithError(Exception())
        store.completeDeletionWithError(deletionError)

        val receivedResult = Result.runCatching { sut.validateCache() }

        assertEquals(Result.failure(deletionError), receivedResult)
    }

    // region Helpers

    private fun makeSut(
        currentTimestamp: () -> Instant = Clock.System::now,
    ): Pair<LocalFeedRepository, FeedStoreSpy> {
        val store = FeedStoreSpy()
        val sut = LocalFeedRepository(store, currentTimestamp = { currentTimestamp().epochSeconds })

        return sut to store
    }

    private fun LocalFeedRepository.expectLoad(
        expectedResult: Result<List<FeedPhoto>>,
        onAction: () -> Unit,
    ) {
        onAction()

        val receivedResult = load()

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
            RETRIEVE, DELETE_CACHED_FEED,
        }

        val receivedMessages = mutableListOf<Message>()

        private var retrievalResult: Result<CachedFeed?>? = null
        private var deletionResult: Result<Unit?>? = null

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

        override fun deleteCachedFeed() {
            receivedMessages.add(DELETE_CACHED_FEED)
            return deletionResult?.getOrThrow() ?: Unit
        }

        fun completeDeletionWithError(error: Exception) {
            deletionResult = Result.failure(error)
        }
    }

    // endregion
}
