package com.greenpeaks.photofeed.util

import platform.Foundation.NSUUID

class NSUUIDGenerator : UuidGenerator {
    override fun randomUuidString() = NSUUID().UUIDString()

    override fun isValidUuidValue(value: String) = try {
        NSUUID(value)
        true
    } catch (exception: Exception) {
        false
    }
}

internal actual object UuidGeneratorFactory {
    actual fun create(): UuidGenerator = NSUUIDGenerator()
}
