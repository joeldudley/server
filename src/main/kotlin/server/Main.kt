package server

import server.request.Method
import server.request.Request

internal val PORT = 4444

fun main(args: Array<String>) {
    val myServer = Server(PORT)

    myServer.registerRoute("/", Method.GET, object : Route() {
        override fun dispatch(request: Request, connection: ClientConnection) {
            val body = "GET received"
            // We add one to account for the final new-line.
            val bodyLength = body.length + 1
            val headers = listOf("HTTP/1.1 200 OK", "Content-Type: text/plain", "Content-Length: $bodyLength", "Connection: close")
            connection.writeResponse(headers, body)
        }
    })

    myServer.registerRoute("/", Method.POST, object : Route() {
        override fun dispatch(request: Request, connection: ClientConnection) {
            val body = "POST received"
            // We add one to account for the final new-line.
            val bodyLength = body.length + 1
            val headers = listOf("HTTP/1.1 200 OK", "Content-Type: text/plain", "Content-Length: $bodyLength", "Connection: close")
            connection.writeResponse(headers, body)
        }
    })

    myServer.start()
}