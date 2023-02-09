package com.asturiancoder.photofeed.feed.api

interface HttpClient {
    fun get(url: String): Result<HttpResponse>
}