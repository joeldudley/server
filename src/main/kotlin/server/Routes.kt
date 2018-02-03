package server

import server.request.Method
import server.request.Request

internal val GET_RECEIVED_ROUTE = Route("/", Method.GET, object : Handler {
    override fun dispatch(request: Request): Pair<List<String>, String> {
        val body = "GET received"
        // We add one to account for the final new-line.
        val bodyLength = body.length + 1
        val headers = listOf("HTTP/1.1 200 OK", "Content-Type: text/plain", "Content-Length: $bodyLength", "Connection: close")
        return headers to body
    }
})

internal val POST_RECEIVED_ROUTE = Route("/", Method.POST, object : Handler {
    override fun dispatch(request: Request): Pair<List<String>, String> {
        val body = "POST received"
        // We add one to account for the final new-line.
        val bodyLength = body.length + 1
        val headers = listOf("HTTP/1.1 200 OK", "Content-Type: text/plain", "Content-Length: $bodyLength", "Connection: close")
        return headers to body
    }
})