package server

val expectedGetRootRouteResponse = "HTTP/1.1 200 OK\nContent-Type: text/plain\nContent-Length: 13\nConnection: close\n\nGET received\n"
val expectedPostRootRouteResponse = "HTTP/1.1 200 OK\nContent-Type: text/plain\nContent-Length: 14\nConnection: close\n\nPOST received\n"

val getRootRoute = object : Route() {
    override fun dispatch(request: server.request.Request, connection: ClientConnection) {
        val body = "GET received"
        // We add one to account for the final new-line.
        val bodyLength = body.length + 1
        val headers = listOf("HTTP/1.1 200 OK", "Content-Type: text/plain", "Content-Length: $bodyLength", "Connection: close")
        connection.writeResponse(headers, body)
    }
}

val postRootRoute = object : Route() {
    override fun dispatch(request: server.request.Request, connection: ClientConnection) {
        val body = "POST received"
        // We add one to account for the final new-line.
        val bodyLength = body.length + 1
        val headers = listOf("HTTP/1.1 200 OK", "Content-Type: text/plain", "Content-Length: $bodyLength", "Connection: close")
        connection.writeResponse(headers, body)
    }
}