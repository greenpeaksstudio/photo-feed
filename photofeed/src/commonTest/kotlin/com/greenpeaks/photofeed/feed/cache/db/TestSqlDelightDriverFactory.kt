package com.greenpeaks.photofeed.feed.cache.db

import app.cash.sqldelight.db.SqlDriver

expect object TestSqlDelightDriverFactory {
    fun create(): SqlDriver
}
