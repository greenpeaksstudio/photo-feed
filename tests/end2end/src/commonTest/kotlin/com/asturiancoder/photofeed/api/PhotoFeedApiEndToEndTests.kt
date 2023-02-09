package com.asturiancoder.photofeed.api

import com.asturiancoder.photofeed.feed.api.KtorHttpClient
import com.asturiancoder.photofeed.feed.api.RemoteFeedLoader
import com.asturiancoder.photofeed.feed.feature.FeedPhoto
import io.ktor.client.HttpClient
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class PhotoFeedApiEndToEndTests {

    @Test
    fun endToEndTestServerGETFeedResult_matchesFixedTestData() {
        val url = "http://asturiancoder.github.io/api/test/feed"
        val ktorClient = HttpClient()
        val client = KtorHttpClient(ktorClient)
        val loader = RemoteFeedLoader(url, client)

        val result= loader.load()

        result.fold(
            onSuccess = {photoFeed ->
                assertEquals(photoFeed.count(), 8, "Expected 8 images in the test data photo feed")
                assertEquals(expectedPhotoAt(0), photoFeed[0])
                assertEquals(expectedPhotoAt(1), photoFeed[1])
                assertEquals(expectedPhotoAt(2), photoFeed[2])
                assertEquals(expectedPhotoAt(3), photoFeed[3])
                assertEquals(expectedPhotoAt(4), photoFeed[4])
                assertEquals(expectedPhotoAt(5), photoFeed[5])
                assertEquals(expectedPhotoAt(6), photoFeed[6])
                assertEquals(expectedPhotoAt(7), photoFeed[7])
            },
            onFailure = {
                fail("Expected successful photo feed result, got $it instead")
            }
        )
    }

    // region Helpers

    private fun expectedPhotoAt(index: Int): FeedPhoto {
        return FeedPhoto(
            id = idAt(index),
            description = descriptionAt(index),
            location = locationAt(index),
            url = imageUrlAt(index),
            likes = index + 1,
            author = authorAt(index)
        )
    }

    private fun idAt(index: Int): String {
        return listOf(
            "73A7F70C-75DA-4C2E-B5A3-EED40DC53AA6",
            "BA298A85-6275-48D3-8315-9C8F7C1CD109",
            "5A0D45B3-8E26-4385-8C5D-213E160A5E3C",
            "FF0ECFE2-2879-403F-8DBE-A83B4010B340",
            "DC97EF5E-2CC9-4905-A8AD-3C351C311001",
            "557D87F1-25D3-4D77-82E9-364B2ED9CB30",
            "A83284EF-C2DF-415D-AB73-2A9B8B04950B",
            "F79BD7F8-063F-46E2-8147-A67635C3BB01"
        )[index]
    }

    private fun descriptionAt(index: Int): String? {
        return listOf(
            "Description 1",
            null,
            "Description 3",
            null,
            "Description 5",
            "Description 6",
            "Description 7",
            "Description 8"
        )[index]
    }

    private fun locationAt( index: Int) : String?
    {
        return listOf(
            "Location 1",
            "Location 2",
            null,
            null,
            "Location 5",
            "Location 6",
            "Location 7",
            "Location 8"
        )[index]
    }

    private fun imageUrlAt(index: Int): String {
        return "https://url-${index + 1}.com"
    }

    private fun authorAt(index: Int) : FeedPhoto.Author {
        return FeedPhoto.Author(
            name= nameAt(index),
            imageUrl = authorUrlAt(index)
        )
    }

    private fun nameAt(index: Int): String {
        return "author ${index + 1}"
    }

    private fun authorUrlAt(index: Int): String {
        return "https://author-url-${index + 1}.com"
    }

    // endregion
}