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
            store.retrieve()
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
        val store = FeedStoreSpy()

        LocalFeedLoader(store)

        assertEquals(0, store.receivedMessages.count())
    }

    @Test
    fun load_requestsCacheRetrieval() {
        val store = FeedStoreSpy()
        val sut = LocalFeedLoader(store)

        sut.load()

        assertEquals(listOf(RETRIEVE), store.receivedMessages)
    }

    @Test
    fun load_failsOnRetrievalError() {
        val store = FeedStoreSpy()
        val sut = LocalFeedLoader(store)
        val retrievalError = Exception()

        store.completeRetrievalWithError(retrievalError)
        val receivedResult = sut.load()

        assertEquals(Result.failure(retrievalError), receivedResult)
    }

    // region Helpers

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

        enum class Message {
            RETRIEVE,
        }
    }

    // endregion
}
