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
                println("Handling request on thread ${Thread.currentThread().id}")
                val connectionInputStream = connection.getInputStream()
                val connectionReader = connectionInputStream.bufferedReader()

                val connectionOutputStream = connection.getOutputStream()
                val connectionWriter = connectionOutputStream.bufferedWriter()

                val requestHeaders = mutableListOf<String>()
                while (true) {
                    val line = connectionReader.readLine()
                    if (line == "") {
                        break
                    }
                    requestHeaders += line
                }

                val additionalText = listOf("Echoing request:")

                val responseBody = additionalText + requestHeaders
                val responseLength = responseBody.sumBy { it.length }

                // TODO: Is provided length correct?
                val headers = listOf("HTTP/1.1 200 OK", "Content-Type: text/plain", "Content-Length: $responseLength", "Connection: close")
                for (header in headers) {
                    connectionWriter.write(header)
                    connectionWriter.newLine()
                    connectionWriter.flush()
                }

                connectionWriter.newLine()
                connectionWriter.flush()

                for (line in responseBody) {
                    connectionWriter.write(line)
                    connectionWriter.newLine()
                    connectionWriter.flush()
                }

                connection.close()
            }
        }
    }
}