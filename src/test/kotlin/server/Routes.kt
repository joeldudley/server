package server

val expectedGetRootHandlerHeaders = listOf("HTTP/1.1 200 OK", "Content-Type: text/plain", "Content-Length: 13", "Connection: close")
val expectedGetRootHandlerBody = "GET received"
val expectedPostRootHandlerHeaders = listOf("HTTP/1.1 200 OK", "Content-Type: text/plain", "Content-Length: 14", "Connection: close")
val expectedPostRootHandlerBody = "POST received"
val expectedUnrecognisedRouteHandlerHeaders = listOf("HTTP/1.1 500 Internal Server Error", "Content-Type: text/plain", "Content-Length: 19", "Connection: close")
val expectedUnrecognisedRouteHandlerBody = "Unrecognised route"

val getRootHandler = object : Handler {
    override fun dispatch(request: server.request.Request): Pair<List<String>, String> {
        val body = "GET received"
        // We add one to account for the final new-line.
        val bodyLength = body.length + 1
        val headers = listOf("HTTP/1.1 200 OK", "Content-Type: text/plain", "Content-Length: $bodyLength", "Connection: close")
        return headers to body
    }
}

val postRootHandler = object : Handler {
    override fun dispatch(request: server.request.Request): Pair<List<String>, String> {
        val body = "POST received"
        // We add one to account for the final new-line.
        val bodyLength = body.length + 1
        val headers = listOf("HTTP/1.1 200 OK", "Content-Type: text/plain", "Content-Length: $bodyLength", "Connection: close")
        return headers to body
    }
}