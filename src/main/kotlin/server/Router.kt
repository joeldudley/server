package server

import server.request.Method
import server.request.Request

/**
 * Dictates how the server handles specific HTTP requests.
 *
 * Individual [Handler]s are installed using the [registerHandler] method.
 */
class Router {
    private val routes = mutableMapOf<Pair<String, Method>, Handler>()

    /**
     * Adds a [Handler] to the router.
     *
     * @param path the path the route handles.
     * @param method the HTTP method the route handles.
     * @param handler how to respond to the request.
     */
    fun registerHandler(path: String, method: Method, handler: Handler) {
        routes[path to method] = handler
    }

    /**
     * Routes an HTTP request to the correct [Handler].
     *
     * @param request the HTTP request.
     * @return The headers and body of the HTTP response.
     */
    fun handleConnection(request: Request): Pair<List<String>, String> {
        // TODO: Introduce error handlers for unrecognised routes
        val route = routes[request.path to request.method] ?: throw UnregisteredRouteException()
        return route.dispatch(request)
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

class UnregisteredRouteException: IllegalArgumentException()