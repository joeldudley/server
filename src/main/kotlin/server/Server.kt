package server

import server.request.Method
import java.net.ServerSocket
import java.net.SocketException
import java.util.concurrent.Executors

/**
 * A server.
 *
 * @param port The port the server listens on.
 * @param numberOfThreads The number of threads the server uses to handle requests.
 */
class Server(private val port: Int, numberOfThreads: Int = 10) {
    // The server socket listens for localhost connections on the specified port.
    private val serverSocket = ServerSocket(port)
    // The threads for handling HTTP requests.
    private val threadPool = Executors.newFixedThreadPool(numberOfThreads)
    // Prepares a response to HTTP requests based on their path.
    private val router = Router()

    /**
     * Adds a route to the router.
     *
     * @param path the path the route handles.
     * @param method the HTTP method the route handles.
     * @param route how to respond to the request.
     */
    fun registerRoute(path: String, method: Method, route: Route) {
        router.registerRoute(path, method, route)
    }

    /** Starts the server running in a loop. */
    fun start() {
        while (true) {
            val connection = try {
                serverSocket.accept()
            } catch (e: SocketException) {
                // The server has been shut down.
                break
            }

            // Each request is assigned its own thread.
            threadPool.submit {
                val clientConnection = ClientConnection(connection)
                val request = clientConnection.parseRequest()
                router.handleConnection(request, clientConnection)
                connection.close()
            }
        }
    }

    /** Shuts down the server. */
    fun shutDown() {
        serverSocket.close()
    }
}