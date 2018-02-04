package server

import server.ResponseHeader.*
import server.StatusLine._200

class Response(
        val body: String,
        val statusLine: StatusLine = _200,
        val headers: List<ResponseHeader> = listOf(
                ContentType("text/plain"),
                ContentLength(body.length + 1),
                Connection("close"))) {
    override fun toString() = "$statusLine\n${headers.joinToString("\n")}\n\n$body\n"
}

sealed class StatusLine {
    object _200: StatusLine() {
        override fun toString() = "HTTP/1.1 200 OK"
    }
    object _404: StatusLine() {
        override fun toString() = "HTTP/1.1 404 Not Found"
    }
    object _405: StatusLine() {
        override fun toString() = "HTTP/1.1 405 Method Not Allowed"
    }
}

sealed class ResponseHeader {
    data class ContentType(val value: String): ResponseHeader() {
        override fun toString() = "Content-Type: $value"
    }
    data class ContentLength(val value: Int): ResponseHeader() {
        override fun toString() = "Content-Length: $value"
    }
    data class Connection(val value: String): ResponseHeader() {
        override fun toString() = "Connection: $value"
    }
}