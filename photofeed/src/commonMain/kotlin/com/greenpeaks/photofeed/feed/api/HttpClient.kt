package com.greenpeaks.photofeed.feed.api

import com.greenpeaks.photofeed.feed.api.model.HttpResponse

interface HttpClient {
    fun get(url: String): HttpResponse
}
