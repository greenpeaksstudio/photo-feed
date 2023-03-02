package com.asturiancoder.photofeed.util

import java.util.UUID

class RandomUuidGenerator : UuidGenerator {

    override fun randomUuidString() = UUID.randomUUID().toString()

    override fun isValidUuidValue(value: String) = try {
        UUID.fromString(value)
        true
    } catch (e: Exception) {
        false
    }
}

internal actual object UuidGeneratorFactory {
    actual fun create(): UuidGenerator = RandomUuidGenerator()
}
