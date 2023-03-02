package com.asturiancoder.photofeed.util

import platform.Foundation.NSUUID

class NSUUIDGenerator : UuidGenerator {
    override fun randomUuidString() = NSUUID().UUIDString()

    override fun isValidUuidValue(value: String) = try {
        NSUUID(value)
        true
    } catch (e: Exception) {
        false
    }
}

internal actual object UuidGeneratorFactory {
    actual fun create(): UuidGenerator = NSUUIDGenerator()
}
