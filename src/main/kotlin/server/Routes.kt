package server

internal val GET_RECEIVED_ROUTE = Route("/", Method.GET, object : Handler {
    override fun dispatch(request: Request): Response {
        val body = "GET received"
        // We add one to account for the final new-line.
        val bodyLength = body.length + 1
        val headers = listOf("Content-Type: text/plain", "Content-Length: $bodyLength", "Connection: close")
        return Response(StatusLine._200, headers, body)
    }
})

internal val POST_RECEIVED_ROUTE = Route("/", Method.POST, object : Handler {
    override fun dispatch(request: Request): Response {
        val body = "POST received"
        // We add one to account for the final new-line.
        val bodyLength = body.length + 1
        val headers = listOf("Content-Type: text/plain", "Content-Length: $bodyLength", "Connection: close")
        return Response(StatusLine._200, headers, body)
    }
})