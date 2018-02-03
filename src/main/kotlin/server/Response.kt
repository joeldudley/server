package server

// TODO: Switch to map for headers
// TODO: Switch to enum for headers
class Response(val statusLine: StatusLine, val headers: List<String>, val body: String)

sealed class StatusLine {
    object _200: StatusLine() {
        override fun toString() = "HTTP/1.1 200 OK"
    }
    object _500: StatusLine() {
        override fun toString() = "HTTP/1.1 500 Internal Server Error"
    }
}