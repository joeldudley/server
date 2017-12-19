package server

import java.net.ServerSocket
import java.util.concurrent.Executors

/** A server that echoes what's written to the [socket]. */
class Server(private val socket: Int, numberOfThreads: Int = 10) {
    // A socket that listens for localhost connections.
    private val serverSocket = ServerSocket(socket)
    private val threadPool = Executors.newFixedThreadPool(numberOfThreads)

    /** Starts the server running in a loop. */
    fun start() {
        while (true) {
            val connection = serverSocket.accept()

            threadPool.submit {
                val clientConnection = ClientConnection(connection)
                clientConnection.handleConnection()
                clientConnection.close()
            }
        }
    }

    fun shutDown() {
        serverSocket.close()
    }
}