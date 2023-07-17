package com.greenpeaks.photofeed.feed.cache.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import app.cash.sqldelight.driver.native.wrapConnection
import co.touchlab.sqliter.DatabaseConfiguration

actual object TestSqlDelightDriverFactory {

    actual fun create(): SqlDriver {
        val schema = PhotoFeedDB.Schema
        return NativeSqliteDriver(
            DatabaseConfiguration(
                name = null,
                version = schema.version,
                create = { connection -> wrapConnection(connection) { schema.create(it) } },
                inMemory = true,
            ),
        )
    }
}
