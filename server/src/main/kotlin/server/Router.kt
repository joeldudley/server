package server

import server.Method.UNKNOWN

/**
 * Dictates how the server handles specific HTTP requests.
 */
class Router(routes: List<Route>) {
    private val routeMap = mutableMapOf<String, MutableMap<Method, Handler>>()

    companion object {
        val _404HandlerBody = "Not found."
        val _405HandlerBody = "Method not allowed."
        val shutdownBody = "Server shut down."
    }

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
        if (request.path == "/shutdown") return Response(shutdownBody)
        if (request.method == UNKNOWN) return Response(_405HandlerBody, StatusLine._405)
        val methodToHandlerMap = routeMap[request.path] ?: return Response(_404HandlerBody, StatusLine._404)
        val handler = methodToHandlerMap[request.method] ?: return Response(_405HandlerBody, StatusLine._405)
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