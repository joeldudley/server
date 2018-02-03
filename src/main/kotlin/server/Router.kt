package server

import server.ResponseHeader.*

/**
 * Dictates how the server handles specific HTTP requests.
 */
class Router(routes: List<Route>) {
    private val routeMap = mutableMapOf<String, MutableMap<Method, Handler>>()

    init {
        routes.forEach { (path, method, handler) ->
            val methodToHandlerMap = routeMap[path]
            if (methodToHandlerMap == null) {
                routeMap[path] = mutableMapOf(method to handler)
            } else {
                methodToHandlerMap.put(method, handler)
            }
        }
    }

    /**
     * Routes an HTTP request to its assigned [Handler].
     *
     * @param request the HTTP request.
     * @return The headers and body of the HTTP response.
     */
    fun handleConnection(request: Request): Response {
        val methodToHandlerMap = routeMap[request.path] ?: return Response(StatusLine._404, _404HandlerHeaders, _404HandlerBody)
        val handler = methodToHandlerMap[request.method] ?: return Response(StatusLine._405, _405HandlerHeaders, _405HandlerBody)
        return handler.dispatch(request)
    }
}

/**
 * Represents an individual route.
 *
 * The route defines how an HTTP request is handled by overriding [dispatch].
 */
interface Handler {
    /**
     * Provides the HTTP response to an HTTP request.
     *
     * @param request the HTTP request.
     * @return The headers and body of the HTTP response.
     */
    fun dispatch(request: Request): Response
}

/**
 * A route.
 *
 * @param path the path the route handles.
 * @param method the HTTP method the route handles.
 * @param handler how to respond to the request.
 */
data class Route(
        val path: String,
        val method: Method,
        val handler: Handler
)

val _404HandlerBody = "Not found"
val _404HandlerHeaders = listOf(
        ContentType("text/plain"),
        ContentLength(_404HandlerBody.length + 1),
        Connection("close"))

val _405HandlerBody = "Method not allowed"
val _405HandlerHeaders = listOf(
        ContentType("text/plain"),
        ContentLength(_405HandlerBody.length + 1),
        Connection("close"))