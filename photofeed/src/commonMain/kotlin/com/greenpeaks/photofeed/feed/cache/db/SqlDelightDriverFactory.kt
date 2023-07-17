package com.greenpeaks.photofeed.feed.cache.db

import app.cash.sqldelight.db.SqlDriver

internal const val DB_NAME = "photo_feed.db"

expect class SqlDelightDriverFactory {
    fun create(): SqlDriver
}
