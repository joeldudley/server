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

            val request = parseRequest(connection)

            when (request.method) {
                "GET" -> writeResponse(connection, "GET received")
                "POST" -> writeResponse(connection, "POST received")
            }

            connection.close()
        }
    }

    internal data class Request(val method: String, val path: String, val protocol: String, val headers: Map<String, String>)

    internal fun parseRequest(connection: Socket): Request {
        val connectionReader = createConnectionReader(connection)
        return parseRequestFromConnectionReader(connectionReader)
    }

    private fun createConnectionReader(connection: Socket): BufferedReader {
        val connectionInputStream = connection.getInputStream()
        return connectionInputStream.bufferedReader()
    }

    private fun parseRequestFromConnectionReader(connectionReader: BufferedReader): Request {
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

    internal fun writeResponse(connection: Socket, body: String) {
        val connectionOutputStream = connection.getOutputStream()
        val connectionWriter = connectionOutputStream.bufferedWriter()

        // We add one to account for the final new-line.
        val bodyLength = body.length + 1
        val responseHeaders = listOf("HTTP/1.1 200 OK", "Content-Type: text/plain", "Content-Length: $bodyLength", "Connection: close")
        for (header in responseHeaders) {
            connectionWriter.write(header)
            connectionWriter.newLine()
            connectionWriter.flush()
        }
        connectionWriter.newLine()
        connectionWriter.write(body)
        connectionWriter.newLine()
        connectionWriter.flush()
    }
}