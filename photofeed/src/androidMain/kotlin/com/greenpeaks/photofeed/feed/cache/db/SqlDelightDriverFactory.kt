package com.greenpeaks.photofeed.feed.cache.db

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

actual class SqlDelightDriverFactory(
    private val context: Context,
) {

    actual fun create(): SqlDriver {
        return AndroidSqliteDriver(
            schema = PhotoFeedDB.Schema,
            context = context,
            name = DB_NAME,
        )
    }
}
