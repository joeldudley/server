package server

val expectedGetRootRouteHeaders = listOf("HTTP/1.1 200 OK", "Content-Type: text/plain", "Content-Length: 13", "Connection: close")
val expectedGetRootRouteBody = "GET received"
val expectedPostRootRouteHeaders = listOf("HTTP/1.1 200 OK", "Content-Type: text/plain", "Content-Length: 14", "Connection: close")
val expectedPostRootRouteBody = "POST received"

val getRootRoute = object : Route() {
    override fun dispatch(request: server.request.Request): Pair<List<String>, String> {
        val body = "GET received"
        // We add one to account for the final new-line.
        val bodyLength = body.length + 1
        val headers = listOf("HTTP/1.1 200 OK", "Content-Type: text/plain", "Content-Length: $bodyLength", "Connection: close")
        return headers to body
    }
}

val postRootRoute = object : Route() {
    override fun dispatch(request: server.request.Request): Pair<List<String>, String> {
        val body = "POST received"
        // We add one to account for the final new-line.
        val bodyLength = body.length + 1
        val headers = listOf("HTTP/1.1 200 OK", "Content-Type: text/plain", "Content-Length: $bodyLength", "Connection: close")
        return headers to body
    }
}