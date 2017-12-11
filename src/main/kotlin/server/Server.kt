package server

import java.io.BufferedReader
import java.io.BufferedWriter
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.Executors

/** A server that echoes what's written to the [socket]. */
class Server(private val socket: Int, numberOfThreads: Int = 10) {
    private val threadPool = Executors.newFixedThreadPool(numberOfThreads)

    /** Starts the server running in a loop. */
    fun start() {
        // Listens for localhost connections.
        val socket = ServerSocket(socket)

        while (true) {
            val connection = socket.accept()
            handleConnection(connection)
        }
    }

    private fun handleConnection(connection: Socket) {
        threadPool.submit {

            val connectionInputStream = connection.getInputStream()
            val connectionReader = connectionInputStream.bufferedReader()
            val request = parseRequest(connectionReader)

            val connectionOutputStream = connection.getOutputStream()
            val connectionWriter = connectionOutputStream.bufferedWriter()

            writeResponseHeaders(connectionWriter)

            when (request.method) {
                "GET" -> {
                    connectionWriter.write("GET received")
                    connectionWriter.newLine()
                    connectionWriter.flush()
                }
                "POST" -> {
                    connectionWriter.write("POST received")
                    connectionWriter.newLine()
                    connectionWriter.flush()
                }
            }

            connection.close()
        }
    }

    internal data class Request(val method: String, val path: String, val protocol: String, val headers: Map<String, String>)

    internal fun parseRequest(connectionReader: BufferedReader): Request {
        val (method, path, protocol) = extractRequestLine(connectionReader)
        val headers = extractHeaders(connectionReader)
        return Request(method, path, protocol, headers)
    }

    private fun extractRequestLine(connectionReader: BufferedReader): Triple<String, String, String> {
        val requestLine = connectionReader.readLine()
        val requestLineRegex = Regex("""[^ ]+""")
        val requestLineMatchResults = requestLineRegex.findAll(requestLine)
        val requestLineItems = requestLineMatchResults.map { it.value }.toList()
        if (requestLineItems. size != 3) {
            throw IllegalArgumentException("Poorly formed HTTP request - request line doesn't contain exactly three items.")
        }
        val (method, path, protocol) = requestLineItems
        return Triple(method, path, protocol)
    }

    private fun extractHeaders(connectionReader: BufferedReader): Map<String, String> {
        val headers = mutableMapOf<String, String>()

        while (true) {
            val line = connectionReader.readLine() ?:
                    throw IllegalArgumentException("Poorly formed HTTP request - no blank line after headers.")

            if (line == "") {
                break
            }

            if (!line.contains(':')) {
                throw IllegalArgumentException("Poorly formed HTTP request - no separating colon.")
            }

            val (header, value) = line.split(':', limit = 2).map { it.trim() }
            headers.put(header.trim(), value.trim())
        }

        return headers
    }

    // TODO: Add tests.
    // TODO: Fix length.
    internal fun writeResponseHeaders(connectionWriter: BufferedWriter) {
        val responseHeaders = listOf("HTTP/1.1 200 OK", "Content-Type: text/plain", "Content-Length: 100", "Connection: close")
        for (header in responseHeaders) {
            connectionWriter.write(header)
            connectionWriter.newLine()
            connectionWriter.flush()
        }
        connectionWriter.newLine()
        connectionWriter.flush()
    }
}