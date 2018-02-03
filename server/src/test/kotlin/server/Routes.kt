package server

import server.ResponseHeader.*

val expectedGetRootHandlerBody = "GET received"
val expectedGetRootHandlerHeaders = listOf(ContentType("text/plain"), ContentLength(expectedGetRootHandlerBody.length + 1), Connection("close"))
val expectedPostRootHandlerBody = "POST received"
val expectedPostRootHandlerHeaders = listOf(ContentType("text/plain"), ContentLength(expectedPostRootHandlerBody.length + 1), Connection("close"))

val getRootHandler = object : Handler {
    override fun dispatch(request: Request) = Response(StatusLine._200, expectedGetRootHandlerHeaders, expectedGetRootHandlerBody)
}

val postRootHandler = object : Handler {
    override fun dispatch(request: Request) = Response(StatusLine._200, expectedPostRootHandlerHeaders, expectedPostRootHandlerBody)
}