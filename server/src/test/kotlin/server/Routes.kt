package server

val expectedGetRootHandlerBody = "GET received"
val expectedPostRootHandlerBody = "POST received"

val getRootHandler = object : Handler {
    override fun dispatch(request: Request) = Response(expectedGetRootHandlerBody)
}

val postRootHandler = object : Handler {
    override fun dispatch(request: Request) = Response(expectedPostRootHandlerBody)
}