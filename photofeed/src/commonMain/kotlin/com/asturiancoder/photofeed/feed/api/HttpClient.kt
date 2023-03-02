package com.asturiancoder.photofeed.feed.api

import com.asturiancoder.photofeed.feed.api.model.HttpResponse

interface HttpClient {
    fun get(url: String): Result<HttpResponse>
}
