package com.asturiancoder.photofeed.feed.cache.db

import com.asturiancoder.photofeed.feed.cache.model.LocalFeedPhoto
import kotlin.test.Test
import kotlin.test.assertEquals

class FeedColumnAdapterTests {

    @Test
    fun encode_deliversJsonEncodedString() {
        val sut = FeedColumnAdapter()

        val encodedValue = sut.encode(localFeed)

        assertEquals(encodedFeed, encodedValue)
    }

    @Test
    fun decode_deliversEmptyLocalFeedFromEmptyString() {
        val sut = FeedColumnAdapter()

        val decodedValue = sut.decode("")

        assertEquals(emptyList(), decodedValue)
    }

    @Test
    fun decode_deliversLocalFeedFromString() {
        val sut = FeedColumnAdapter()

        val decodedValue = sut.decode(encodedFeed)

        assertEquals(localFeed, decodedValue)
    }

    // region Helpers

    private val encodedFeed =
        "[{\"id\":\"B4AFA693-3719-4EEF-B83C-7542C3A109C3\",\"description\":\"A description\"," +
            "\"location\":\"A location\",\"url\":\"http://a-url.com\",\"likes\":0," +
            "\"authorName\":\"An author\",\"authorImageUrl\":\"https://an-author-url.com\"}]"

    private val localFeed = listOf(
        LocalFeedPhoto(
            id = "B4AFA693-3719-4EEF-B83C-7542C3A109C3",
            description = "A description",
            location = "A location",
            url = "http://a-url.com",
            likes = 0,
            authorName = "An author",
            authorImageUrl = "https://an-author-url.com",
        ),
    )

    // endregion
}
