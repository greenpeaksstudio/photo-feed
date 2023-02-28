package com.asturiancoder.photofeed.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

class UuidTests {

    @Test
    fun fromValue_returnsUuidOnValidValue() {
        val uuidString = validUUIDString()

        val uuid = Uuid.from(uuidString)

        assertEquals(uuidString, uuid?.uuidString)
    }

    @Test
    fun fromValue_returnsNullOnInvalidValue() {
        val uuidString = "invalid UUID"

        val uuid = Uuid.from(uuidString)

        assertNull(uuid)
    }

    @Test
    fun equals_returnsFalseWhenComparingNonUuidType() {
        val uuid = Uuid.from(validUUIDString())!!

        assertFalse(uuid.equals("null"))
    }

    @Test
    fun equals_returnsFalseWhenComparingNull() {
        val uuid = Uuid.from(validUUIDString())!!

        assertFalse(uuid.equals(null))
    }

    @Test
    fun equals_returnsFalseOnNonMatchingUuidStringValues() {
        val uuid = Uuid.from(validUUIDString())!!
        val otherUUID = Uuid.from(anotherValidUUIDString())!!

        assertNotEquals(uuid, otherUUID)
    }

    @Test
    fun equals_returnsTrueOnMatchingUuidStringValues() {
        val uuid = Uuid.from(validUUIDString())!!
        val otherUUID = Uuid.from(validUUIDString())!!

        assertEquals(uuid, otherUUID)
    }

    // region Helpers

    private fun validUUIDString() = "66F5A4F9-53F2-42F9-8C9F-69EFE5F1F1E3"
    private fun anotherValidUUIDString() = "34977d81-936d-4305-bcc6-9640cfaef197"

    // endregion

}