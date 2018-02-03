package server

import server.request.Method
import server.request.Request

private val PORT = 4444

private val getReceivedRoute = Route("/", Method.GET, object : Handler {
    override fun dispatch(request: Request): Pair<List<String>, String> {
        val body = "GET received"
        // We add one to account for the final new-line.
        val bodyLength = body.length + 1
        val headers = listOf("HTTP/1.1 200 OK", "Content-Type: text/plain", "Content-Length: $bodyLength", "Connection: close")
        return headers to body
    }
})

private val postReceivedRoute = Route("/", Method.POST, object : Handler {
    override fun dispatch(request: Request): Pair<List<String>, String> {
        val body = "POST received"
        // We add one to account for the final new-line.
        val bodyLength = body.length + 1
        val headers = listOf("HTTP/1.1 200 OK", "Content-Type: text/plain", "Content-Length: $bodyLength", "Connection: close")
        return headers to body
    }
})

private class MyServer(port: Int) : Server(port, listOf(getReceivedRoute, postReceivedRoute))

// TODO: Parameterise to not always use localhost.
fun main(args: Array<String>) {
    val port = args[0].toInt()
    val myServer = MyServer(port)
    myServer.start()
}