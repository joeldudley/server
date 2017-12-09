package server

import java.net.ServerSocket
import java.util.concurrent.Executors

/** A server that echoes what's written to the [socket]. */
class Server(socket: Int, numberOfThreads: Int = 10) {
    private val threadPool = Executors.newFixedThreadPool(numberOfThreads)
    // Listens for localhost connections.
    private val socket = ServerSocket(socket)

    /** Starts the server running in a loop. */
    fun start() {
        while (true) {
            val connection = socket.accept()
            threadPool.submit {
                val connectionInputStream = connection.getInputStream()
                val connectionReader = connectionInputStream.bufferedReader()

                val connectionOutputStream = connection.getOutputStream()
                val connectionWriter = connectionOutputStream.bufferedWriter()

                while (true) {
                    val line = connectionReader.readLine() ?: break
                    println(line)
                    connectionWriter.write(line)
                    connectionWriter.newLine()
                    connectionWriter.flush()
                }

                connection.close()
            }
        }
    }
}