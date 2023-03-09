package com.asturiancoder.photofeed.feed.cache

import com.asturiancoder.photofeed.feed.cache.LocalFeedLoaderTests.FeedStoreSpy.Message.RETRIEVE
import com.asturiancoder.photofeed.feed.feature.FeedLoader
import com.asturiancoder.photofeed.feed.feature.FeedPhoto
import kotlin.test.Test
import kotlin.test.assertEquals

internal class LocalFeedLoader(
    private val store: FeedStore,
) : FeedLoader {
    override fun load(): Result<List<FeedPhoto>> {
        try {
            store.retrieve() ?: return Result.success(emptyList())
        } catch (exception: Exception) {
            return Result.failure(exception)
        }
        return Result.failure(Exception())
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

    // region Helpers

    private fun makeSut(): Pair<LocalFeedLoader, FeedStoreSpy> {
        val store = FeedStoreSpy()
        val sut = LocalFeedLoader(store)

        return sut to store
    }

    private class FeedStoreSpy : FeedStore {
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

        enum class Message {
            RETRIEVE,
        }
    }

    // endregion
}
