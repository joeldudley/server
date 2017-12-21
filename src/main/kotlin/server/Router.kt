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
     * @param connection the connection to the client.
     */
    fun handleConnection(request: Request, connection: ClientConnection) {
        // TODO: Introduce error handlers for unrecognised routes
        val route = routes[request.path to request.method] ?: throw UnregisteredRouteException()
        route.dispatch(request, connection)
    }
}

/**
 * Represents an individual route.
 *
 * The route defines how an HTTP request is handled by overriding [dispatch].
 */
abstract class Route {
    /**
     * Handles an HTTP request.
     *
     * @param request the HTTP request.
     * @param connection the connection to the client.
     */
    abstract fun dispatch(request: Request, connection: ClientConnection)
}

class UnregisteredRouteException: IllegalArgumentException()