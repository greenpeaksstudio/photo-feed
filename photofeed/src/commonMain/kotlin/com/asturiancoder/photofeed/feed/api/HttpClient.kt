package com.asturiancoder.photofeed.feed.api

internal interface HttpClient {
    fun get(url: String): Result<HttpResponse>
}