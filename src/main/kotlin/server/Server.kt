package server

import server.request.Method
import java.net.ServerSocket
import java.net.SocketException
import java.util.concurrent.Executors

/** A server that echoes what's written to the [socket]. */
class Server(private val socket: Int, numberOfThreads: Int = 10) {
    // A socket that listens for localhost connections.
    private val serverSocket = ServerSocket(socket)
    private val threadPool = Executors.newFixedThreadPool(numberOfThreads)
    private val router = Router()

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

            threadPool.submit {
                val clientConnection = ClientConnection(connection)
                val request = clientConnection.parseRequest()
                router.handleConnection(request, clientConnection)
                connection.close()
            }
        }
    }

    fun shutDown() {
        serverSocket.close()
    }
}