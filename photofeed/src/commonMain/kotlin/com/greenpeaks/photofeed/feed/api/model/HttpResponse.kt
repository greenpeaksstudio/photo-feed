package com.greenpeaks.photofeed.feed.api.model

data class HttpResponse(
    val code: HttpStatusCode,
    val jsonString: String,
)
