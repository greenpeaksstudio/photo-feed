package com.asturiancoder.photofeed

import com.asturiancoder.photofeed.feed.api.HttpClient
import com.asturiancoder.photofeed.feed.api.HttpResponse
import com.asturiancoder.photofeed.feed.api.RemoteFeedLoader
import com.asturiancoder.photofeed.feed.api.RemoteFeedPhoto
import com.asturiancoder.photofeed.feed.feature.FeedLoader
import com.asturiancoder.photofeed.feed.feature.FeedPhoto
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RemoteFeedLoaderTests {

    @Test
    fun init_doesNotStartRequestFromUrl() {
        val (_, client) = makeSut()

        assertTrue(client.requestedUrls.isEmpty())
    }

    @Test
    fun load_startsRequestFromUrl() {
        val url = "https://a-given-url.com"
        val (sut, client) = makeSut(url)

        sut.load()

        assertEquals(listOf(url), client.requestedUrls)
    }

    @Test
    fun load_twice_startsRequestFromUrlTwice() {
        val url = "https://a-given-url.com"
        val (sut, client) = makeSut(url)

        sut.load()
        sut.load()

        assertEquals(listOf(url, url), client.requestedUrls)
    }

    @Test
    fun load_deliversErrorOnClientError() {
        val (sut, client) = makeSut()
        val clientError = Exception()
        client.stubWith(Result.failure(clientError))

        val receivedResult = sut.load()

        assertEquals(Result.failure(RemoteFeedLoader.Error.Connectivity), receivedResult)
    }

    @Test
    fun load_deliversErrorOnNon200HttpResponse() {
        val (sut, client) = makeSut()

        val samples = listOf(199, 201, 300, 400, 500)
        samples.forEach { httpCode ->
            val json = makePhotosJson(listOf())
            client.stubWith(Result.success(HttpResponse(httpCode, json)))
            val receivedResult = sut.load()

            assertEquals(Result.failure(RemoteFeedLoader.Error.InvalidData), receivedResult)
        }
    }

    @Test
    fun load_deliversErrorOn200HttpResponseWithInvalidJson() {
        val invalidJson = "invalidJson"
        val (sut, client) = makeSut()

        client.stubWith(Result.success(HttpResponse(200, jsonString = invalidJson)))
        val receivedResult = sut.load()

        assertEquals(Result.failure(RemoteFeedLoader.Error.InvalidData), receivedResult)
    }

    @Test
    fun load_deliversNoPhotosOn200HttpResponseWithEmptyJsonList() {
        val (sut, client) = makeSut()

        val emptyListJson = makePhotosJson(listOf())
        client.stubWith(Result.success(HttpResponse(code = 200, jsonString = emptyListJson)))
        val receivedResult = sut.load()

        assertEquals(Result.success(listOf()), receivedResult)
    }

    @Test
    fun load_deliversPhotosOn200HttpResponseWithJsonPhotos() {
        val (sut, client) = makeSut()

        val (photo1, remote1) = makePhoto(
            id = "an id",
            url = "http://a-url.com",
            likes = 1,
            authorName = "a name",
            authorImageUrl = "http://an-author-url.com"
        )

        val (photo2, remote2) = makePhoto(
            id = " another id",
            description = "a description",
            location = "a location",
            url = "http://another-url.com",
            likes = 2,
            authorName = "another name",
            authorImageUrl = "http://another-author-url.com"
        )

        val json = makePhotosJson(listOf(remote1, remote2))
        client.stubWith(Result.success(HttpResponse(code = 200, jsonString = json)))
        val receivedResult = sut.load()

        assertEquals(Result.success(listOf(photo1, photo2)), receivedResult)
    }

    // region Helpers

    private fun makeSut(url: String = "https://a-url.com"): Pair<FeedLoader, HttpClientStub> {
        val client = HttpClientStub()
        val sut = RemoteFeedLoader(url, client)

        return sut to client
    }

    private fun makePhoto(
        id: String,
        description: String? = null,
        location: String? = null,
        url: String,
        likes: Int,
        authorName: String,
        authorImageUrl: String
    ): Pair<FeedPhoto, RemoteFeedPhoto> {
        val photo = FeedPhoto(
            id = id,
            description = description,
            location = location,
            url = url,
            likes = likes,
            author = FeedPhoto.Author(name = authorName, imageUrl = authorImageUrl)
        )

        val remote = RemoteFeedPhoto(
            id = id,
            description = description,
            location = location,
            url = url,
            likes = likes,
            author = RemoteFeedPhoto.Author(name = authorName, imageUrl = authorImageUrl)
        )

        return photo to remote
    }

    private fun makePhotosJson(photos: List<RemoteFeedPhoto>): String {
        val root = mapOf("photos" to photos)
        return Json.encodeToString(root)
    }

    private class HttpClientStub : HttpClient {
        val requestedUrls = mutableListOf<String>()

        private val response = HttpResponse(code = 200, jsonString = "")
        private var stub = Result.success(response)

        override fun get(url: String): Result<HttpResponse> {
            requestedUrls.add(url)
            return stub
        }

        fun stubWith(stub: Result<HttpResponse>) {
            this.stub = stub
        }
    }

    // endregion
}