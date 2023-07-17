package com.greenpeaks.photofeed.feed.cache.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

actual class SqlDelightDriverFactory {

    actual fun create(): SqlDriver {
        return NativeSqliteDriver(
            schema = PhotoFeedDB.Schema,
            name = DB_NAME,
        )
    }
}
