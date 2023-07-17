package com.greenpeaks.photofeed.feed.cache.db

import app.cash.sqldelight.ColumnAdapter
import com.greenpeaks.photofeed.feed.cache.model.LocalFeedPhoto
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class FeedColumnAdapter : ColumnAdapter<List<LocalFeedPhoto>, String> {

    override fun decode(databaseValue: String): List<LocalFeedPhoto> {
        return if (databaseValue.isEmpty()) {
            emptyList()
        } else {
            Json.decodeFromString(databaseValue)
        }
    }

    override fun encode(value: List<LocalFeedPhoto>): String {
        return Json.encodeToString(value)
    }
}
