package server

val expectedGetRootHandlerHeaders = listOf("Content-Type: text/plain", "Content-Length: 13", "Connection: close")
val expectedGetRootHandlerBody = "GET received"
val expectedPostRootHandlerHeaders = listOf("Content-Type: text/plain", "Content-Length: 14", "Connection: close")
val expectedPostRootHandlerBody = "POST received"
val expectedUnrecognisedRouteHandlerHeaders = listOf("Content-Type: text/plain", "Content-Length: 19", "Connection: close")
val expectedUnrecognisedRouteHandlerBody = "Unrecognised route"

val getRootHandler = object : Handler {
    override fun dispatch(request: Request): Response {
        val body = "GET received"
        // We add one to account for the final new-line.
        val bodyLength = body.length + 1
        val headers = listOf("Content-Type: text/plain", "Content-Length: $bodyLength", "Connection: close")
        return Response(StatusLine._200, headers, body)
    }
}

val postRootHandler = object : Handler {
    override fun dispatch(request: Request): Response {
        val body = "POST received"
        // We add one to account for the final new-line.
        val bodyLength = body.length + 1
        val headers = listOf("Content-Type: text/plain", "Content-Length: $bodyLength", "Connection: close")
        return Response(StatusLine._200, headers, body)
    }
}