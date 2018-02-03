package server

class Response(val statusLine: StatusLine, val headers: List<ResponseHeader>, val body: String)

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
    class ContentType(private val value: String): ResponseHeader() {
        override fun toString() = "Content-Type: $value"
    }
    class ContentLength(private val value: Int): ResponseHeader() {
        override fun toString() = "Content-Length: $value"
    }
    class Connection(private val value: String): ResponseHeader() {
        override fun toString() = "Connection: $value"
    }
}