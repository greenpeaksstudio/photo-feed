package com.asturiancoder.photofeed.feed.cache

import kotlin.test.Test
import kotlin.test.assertEquals

internal class LocalFeedLoader(
    private val feedStore: FeedStore,
)

internal interface FeedStore

class LocalFeedLoaderTests {

    @Test
    fun init_doesNotMessageStoreUponCreation() {
        val store = FeedStoreSpy()

        LocalFeedLoader(store)

        assertEquals(0, store.messages.count())
    }

    // region Helpers

    private class FeedStoreSpy : FeedStore {
        val messages = emptyList<Message>()

        private enum class Message
    }

    // endregion
}
