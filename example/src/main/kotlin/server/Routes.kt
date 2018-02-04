package server

internal val GET_RECEIVED_ROUTE = Route("/", Method.GET, object : Handler {
    override fun dispatch(request: Request): Response {
        val body = "GET received"
        return Response(body)
    }
})

internal val POST_RECEIVED_ROUTE = Route("/", Method.POST, object : Handler {
    override fun dispatch(request: Request): Response {
        val body = "POST received"
        return Response(body)
    }
})