package com.asturiancoder.photofeed.feed.cache

import kotlin.test.Test
import kotlin.test.assertNull

class SqlDelightFeedStoreTests {

    @Test
    fun retrieve_deliversEmptyOnEmptyCache() {
        val sut = SqlDelightFeedStore()

        val receivedCache = sut.retrieve()

        assertNull(receivedCache)
    }
}
