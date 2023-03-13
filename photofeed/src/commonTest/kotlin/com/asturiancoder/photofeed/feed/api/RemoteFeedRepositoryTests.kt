package com.asturiancoder.photofeed.feed.api

import com.asturiancoder.photofeed.feed.api.RemoteFeedRepository.Error
import com.asturiancoder.photofeed.feed.api.model.HttpResponse
import com.asturiancoder.photofeed.feed.api.model.HttpStatusCode
import com.asturiancoder.photofeed.feed.feature.FeedLoader
import com.asturiancoder.photofeed.feed.feature.FeedPhoto
import com.asturiancoder.photofeed.util.Uuid
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RemoteFeedRepositoryTests {

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

        expect(sut, expectedResult = Result.failure(Error.Connectivity)) {
            client.completeWithError(clientError)
        }
    }

    @Test
    fun load_deliversErrorOnNon200HttpResponse() {
        val (sut, client) = makeSut()

        val samples = listOf(199, 201, 300, 400, 500)
        val json = makePhotosJson(listOf())
        samples.forEach { httpCode ->
            expect(sut, expectedResult = Result.failure(Error.InvalidData)) {
                client.completeWithResponse(makeResponse(httpCode, json))
            }
        }
    }

    @Test
    fun load_deliversErrorOn200HttpResponseWithInvalidJson() {
        val invalidJson = "invalidJson"
        val (sut, client) = makeSut()

        expect(sut, expectedResult = Result.failure(Error.InvalidData)) {
            client.completeWithResponse(makeResponse(200, invalidJson))
        }
    }

    @Test
    fun load_deliversNoPhotosOn200HttpResponseWithEmptyJsonList() {
        val (sut, client) = makeSut()

        expect(sut, expectedResult = Result.success(listOf())) {
            val emptyListJson = makePhotosJson(listOf())
            client.completeWithResponse(makeResponse(200, emptyListJson))
        }
    }

    @Test
    fun load_deliversPhotosOn200HttpResponseWithJsonPhotos() {
        val (sut, client) = makeSut()

        val (photo1, json1) = makePhoto(
            url = "http://a-url.com",
            likes = 1,
            authorName = "a name",
            authorImageUrl = "http://an-author-url.com",
        )

        val (photo2, json2) = makePhoto(
            description = "a description",
            location = "a location",
            url = "http://another-url.com",
            likes = 2,
            authorName = "another name",
            authorImageUrl = "http://another-author-url.com",
        )

        expect(sut, expectedResult = Result.success(listOf(photo1, photo2))) {
            val json = makePhotosJson(listOf(json1, json2))
            client.completeWithResponse(makeResponse(200, json))
        }
    }

    // region Helpers

    private fun makeSut(url: String = "https://a-url.com"): Pair<FeedLoader, HttpClientSpy> {
        val client = HttpClientSpy()
        val sut = RemoteFeedRepository(url, client)

        return sut to client
    }

    private fun expect(
        sut: FeedLoader,
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

    private fun makeResponse(httpCode: Int, json: String): HttpResponse {
        return HttpResponse(HttpStatusCode.fromValue(httpCode), json)
    }

    private fun makePhoto(
        id: Uuid = Uuid(),
        description: String? = null,
        location: String? = null,
        url: String,
        likes: Int,
        authorName: String,
        authorImageUrl: String,
    ): Pair<FeedPhoto, JsonObject> {
        val photo = FeedPhoto(
            id = id,
            description = description,
            location = location,
            url = url,
            likes = likes,
            author = FeedPhoto.Author(name = authorName, imageUrl = authorImageUrl),
        )

        val json = JsonObject(
            mapOf(
                "id" to JsonPrimitive(id.uuidString),
                "description" to JsonPrimitive(description),
                "location" to JsonPrimitive(location),
                "url" to JsonPrimitive(url),
                "likes" to JsonPrimitive(likes),
                "author" to JsonObject(
                    mapOf(
                        "name" to JsonPrimitive(authorName),
                        "image_url" to JsonPrimitive(authorImageUrl),
                    ),
                ),
            ).filter { it.value !is JsonNull },
        )

        return photo to json
    }

    private fun makePhotosJson(photos: List<JsonObject>): String {
        val root = mapOf("photos" to photos)
        return Json.encodeToString(root)
    }

    private class HttpClientSpy : HttpClient {
        val requestedUrls = mutableListOf<String>()

        private val defaultResponse = HttpResponse(code = HttpStatusCode.OK, jsonString = "")
        private var getResult: Result<HttpResponse> = Result.success(defaultResponse)

        override fun get(url: String): HttpResponse {
            requestedUrls.add(url)
            return getResult.getOrThrow()
        }

        fun completeWithError(error: Exception) {
            getResult = Result.failure(error)
        }

        fun completeWithResponse(response: HttpResponse) {
            getResult = Result.success(response)
        }
    }

    // endregion
}
