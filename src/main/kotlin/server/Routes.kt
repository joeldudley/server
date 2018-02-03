package server

import server.ResponseHeader.*

internal val GET_RECEIVED_ROUTE = Route("/", Method.GET, object : Handler {
    override fun dispatch(request: Request): Response {
        val body = "GET received"
        val headers = listOf(ContentType("text/plain"), ContentLength(body.length + 1), Connection("Connection: close"))
        return Response(StatusLine._200, headers, body)
    }
})

internal val POST_RECEIVED_ROUTE = Route("/", Method.POST, object : Handler {
    override fun dispatch(request: Request): Response {
        val body = "POST received"
        val headers = listOf(ContentType("text/plain"), ContentLength(body.length + 1), Connection("Connection: close"))
        return Response(StatusLine._200, headers, body)
    }
})