package com.asturiancoder.photofeed.feed.cache.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver

actual class SqlDelightDriverFactory {

    actual fun create(): SqlDriver {
        return JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).apply {
            PhotoFeedDB.Schema.create(this)
        }
    }
}
