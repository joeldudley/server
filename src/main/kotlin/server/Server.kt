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

            val connectionOutputStream = connection.getOutputStream()
            val connectionWriter = connectionOutputStream.bufferedWriter()

            val requestLine = extractRequestLine(connectionReader)
            val requestHeaders = extractHeaders(connectionReader)

            writeResponseHeaders(connectionWriter)

            when (requestLine.method) {
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

    internal data class RequestLine(val method: String, val path: String, val protocol: String)

    internal fun extractRequestLine(connectionReader: BufferedReader): RequestLine {
        val requestLine = connectionReader.readLine()
        val requestLineItems = requestLine.split(' ')
        if (requestLineItems.size != 3) {
            throw IllegalArgumentException("Poorly formed HTTP request - request line doesn't contain exactly three items.")
        }
        val (method, path, protocol) = requestLineItems
        return RequestLine(method, path, protocol)
    }

    internal fun extractHeaders(connectionReader: BufferedReader): Map<String, String> {
        val headers = mutableMapOf<String, String>()

        while (true) {
            val line = connectionReader.readLine() ?:
                    throw IllegalArgumentException("Poorly formed HTTP request - no blank line after headers.")

            if (line == "") {
                break
            }

            val headerAndValue = line.split(':')

            when (headerAndValue.size) {
                2 -> {
                    val (header, value) = headerAndValue
                    headers.put(header.trim(), value.trim())
                }
                3 -> {
                    if (headerAndValue.first() != "Host") {
                        throw IllegalArgumentException("Poorly formed HTTP request - header doesn't contain exactly two items.")
                    } else {
                        val (header, host, port) = headerAndValue
                        headers.put(header.trim(), (host + port).trim())
                    }
                }
                else -> throw IllegalArgumentException("Poorly formed HTTP request - header doesn't contain exactly two items.")
            }
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