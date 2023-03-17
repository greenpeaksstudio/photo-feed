package com.asturiancoder.photofeed.feed.cache.db

import com.asturiancoder.photofeed.feed.cache.model.LocalFeedPhoto
import kotlin.test.Test
import kotlin.test.assertEquals

class FeedColumnAdapterTests {

    @Test
    fun encode_deliversJsonEncodedString() {
        val localFeed = listOf(localFeedPhoto)
        val sut = FeedColumnAdapter()

        val encodedValue = sut.encode(localFeed)

        assertEquals(encodedFeed, encodedValue)
    }

    // region Helpers

    private val encodedFeed =
        """[{"id":"B4AFA693-3719-4EEF-B83C-7542C3A109C3","description":"A description","location":"A location","url":"http://a-url.com","likes":0,"authorName":"An author","authorImageUrl":"https://an-author-url.com"}]"""

    private val localFeedPhoto = LocalFeedPhoto(
        id = "B4AFA693-3719-4EEF-B83C-7542C3A109C3",
        description = "A description",
        location = "A location",
        url = "http://a-url.com",
        likes = 0,
        authorName = "An author",
        authorImageUrl = "https://an-author-url.com",
    )

    // endregion
}
