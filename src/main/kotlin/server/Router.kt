package server

import server.request.Method
import server.request.Request

class Router {
    private val routes = mutableMapOf<Pair<String, Method>, Route>()

    fun registerRoute(path: String, method: Method, route: Route) {
        routes[path to method] = route
    }

    // TODO: Introduce error handlers for unrecognised routes

    fun handleConnection(request: Request, connection: ClientConnection) {
        val route = routes[request.path to request.method] ?: throw UnregisteredRouteException()
        route.dispatch(request, connection)
    }
}

abstract class Route {
    abstract fun dispatch(request: Request, connection: ClientConnection)
}

class UnregisteredRouteException: IllegalArgumentException()