package server

import server.ResponseHeader.*

val expectedGetRootHandlerHeaders = listOf(ContentType("text/plain"), ContentLength(13), Connection("close"))
val expectedGetRootHandlerBody = "GET received"
val expectedPostRootHandlerHeaders = listOf(ContentType("text/plain"), ContentLength(14), Connection("close"))
val expectedPostRootHandlerBody = "POST received"

val getRootHandler = object : Handler {
    override fun dispatch(request: Request) = Response(StatusLine._200, expectedGetRootHandlerHeaders, expectedGetRootHandlerBody)
}

val postRootHandler = object : Handler {
    override fun dispatch(request: Request) = Response(StatusLine._200, expectedPostRootHandlerHeaders, expectedPostRootHandlerBody)
}