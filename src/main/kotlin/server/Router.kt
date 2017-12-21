package server

import server.request.Method
import server.request.Request

/**
 * Dictates how the server handles specific HTTP requests.
 *
 * Individual [Route]s are installed using the [registerRoute] method.
 */
class Router {
    private val routes = mutableMapOf<Pair<String, Method>, Route>()

    /**
     * Adds a [Route] to the router.
     *
     * @param path the path the route handles.
     * @param method the HTTP method the route handles.
     * @param route how to respond to the request.
     */
    fun registerRoute(path: String, method: Method, route: Route) {
        routes[path to method] = route
    }

    /**
     * Routes an HTTP request to the correct [Route].
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
abstract class Route {
    /**
     * Provides the HTTP response to an HTTP request.
     *
     * @param request the HTTP request.
     * @return The headers and body of the HTTP response.
     */
    abstract fun dispatch(request: Request): Pair<List<String>, String>
}

class UnregisteredRouteException: IllegalArgumentException()