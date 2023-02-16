package com.asturiancoder.photofeed.feed.api.model

data class HttpStatusCode(val value: Int, val description: String) {

    companion object {
        val OK: HttpStatusCode = HttpStatusCode(200, "OK")

        private val httpStatusCodesMap: Map<Int, HttpStatusCode> = allStatusCodes().associateBy { it.value }

        fun fromValue(value: Int): HttpStatusCode {
            return httpStatusCodesMap[value] ?: HttpStatusCode(value, "Unknown Status Code")
        }
    }
}

private fun allStatusCodes(): List<HttpStatusCode> = listOf(
    HttpStatusCode.OK
)