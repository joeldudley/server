package server

import server.request.Method
import java.net.ServerSocket
import java.net.SocketException
import java.util.concurrent.Executors

/**
 * A server.
 *
 * Subclass it to provide the routes that the server supports.
 *
 * @param port The port the server listens on.
 * @param numberOfThreads The number of threads the server uses to handle requests.
 */
abstract class Server(port: Int, routes: List<Route>, numberOfThreads: Int = 10) {
    // The server socket listens for localhost connections on the specified port.
    private val serverSocket = ServerSocket(port)
    // The threads for handling HTTP requests.
    private val threadPool = Executors.newFixedThreadPool(numberOfThreads)
    // Prepares a response to HTTP requests based on their path.
    private val router = Router(routes)

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
                val (responseHeaders, responseBody) = router.handleConnection(request)
                clientConnection.writeResponse(responseHeaders, responseBody)
                connection.close()
            }
        }
    }

    /** Shuts down the server. */
    fun shutDown() {
        serverSocket.close()
    }
}