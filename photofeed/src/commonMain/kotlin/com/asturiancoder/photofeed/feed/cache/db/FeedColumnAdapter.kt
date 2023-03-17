package com.asturiancoder.photofeed.feed.cache.db

import app.cash.sqldelight.ColumnAdapter
import com.asturiancoder.photofeed.feed.cache.model.LocalFeedPhoto
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class FeedColumnAdapter : ColumnAdapter<List<LocalFeedPhoto>, String> {

    override fun decode(databaseValue: String): List<LocalFeedPhoto> {
        TODO("Not yet implemented")
    }

    override fun encode(value: List<LocalFeedPhoto>): String {
        return Json.encodeToString(value)
    }
}
