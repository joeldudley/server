package server

import server.request.Method
import server.request.Request

/**
 * Dictates how the server handles specific HTTP requests.
 */
class Router(routes: List<Route>) {
    private val routeMap = mutableMapOf<Pair<String, Method>, Handler>()

    init {
        routes.forEach { (path, method, handler) ->
            routeMap[path to method] = handler
        }
    }

    /**
     * Routes an HTTP request to its assigned [Handler].
     *
     * @param request the HTTP request.
     * @return The headers and body of the HTTP response.
     */
    fun handleConnection(request: Request): Pair<List<String>, String> {
        val route = routeMap[request.path to request.method]
        return if (route != null) {
            route.dispatch(request)
        } else {
            val body = "Unrecognised route"
            // We add one to account for the final new-line.
            val bodyLength = body.length + 1
            // TODO: Change error message based on unrecognised path vs unrecognised method.
            val headers = listOf("HTTP/1.1 500 Internal Server Error", "Content-Type: text/plain", "Content-Length: $bodyLength", "Connection: close")
            return headers to body
        }
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
    fun dispatch(request: Request): Pair<List<String>, String>
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