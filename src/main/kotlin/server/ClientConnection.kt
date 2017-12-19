package server

import server.request.GetRequest
import server.request.PostRequest
import server.request.Request
import java.net.Socket

class ClientConnection(private val connection: Socket) {
    private val connectionReader = connection.getInputStream().bufferedReader()
    private val connectionWriter = connection.getOutputStream().bufferedWriter()

    fun handleConnection() {
        val request = parseRequest()

        when (request.method) {
            "GET" -> writeResponse("GET received")
            "POST" -> writeResponse("POST received")
        }
    }

    fun close() {
        connection.close()
    }

    fun parseRequest(): Request {
        val (method, path, protocol) = extractRequestLine()
        val headers = extractHeaders()
        return when (method) {
            "GET" -> GetRequest(method, path, protocol, headers)
            "POST" -> {
                val body = extractBody()
                PostRequest(body, method, path, protocol, headers)
            }
            else -> throw IllegalArgumentException("Unrecognised method argument.")
        }
    }

    private fun extractRequestLine(): Triple<String, String, String> {
        val requestLine = connectionReader.readLine()
        val requestLineRegex = Regex("""[^ ]+""")
        val requestLineMatchResults = requestLineRegex.findAll(requestLine)
        val requestLineItems = requestLineMatchResults.map { it.value }.toList()
        if (requestLineItems.size != 3) {
            throw IllegalArgumentException("Poorly formed HTTP request line - request line doesn't contain exactly three items.")
        }
        val (method, path, protocol) = requestLineItems
        return Triple(method, path, protocol)
    }

    private fun extractHeaders(): Map<String, String> {
        val headers = mutableMapOf<String, String>()

        while (true) {
            val line = connectionReader.readLine() ?:
                    throw IllegalArgumentException("Poorly formed HTTP request headers - no blank line after headers.")

            if (line == "") break

            if (!line.contains(':')) throw IllegalArgumentException("Poorly formed HTTP request headers - no separating colon.")

            val (header, value) = line.split(':', limit = 2).map { it.trim() }
            headers.put(header, value)
        }

        return headers
    }

    private fun extractBody(): Map<String, String> {
        val body = mutableMapOf<String, String>()

        val line = connectionReader.readLine()

        if (line !in listOf("", null)) {
            val namesAndValues = line.split('&')
            for (nameAndValue in namesAndValues) {
                val numberOfSeparators = nameAndValue.count { it == '=' }
                if (numberOfSeparators == 0) throw IllegalArgumentException("Poorly formed HTTP request body - no value.")
                if (numberOfSeparators >= 2) throw IllegalArgumentException("Poorly formed HTTP request body - no name.")

                val (name, value) = nameAndValue.split('=', limit = 2).map { it.trim() }

                if (name in body) throw IllegalArgumentException("Poorly formed HTTP request body - repeated name.")

                body.put(name, value)
            }
        }

        return body
    }

    internal fun writeResponse(body: String) {
        // We add one to account for the final new-line.
        val bodyLength = body.length + 1
        val responseHeaders = listOf("HTTP/1.1 200 OK", "Content-Type: text/plain", "Content-Length: $bodyLength", "Connection: close")
        for (header in responseHeaders) {
            connectionWriter.write(header)
            connectionWriter.newLine()
        }
        connectionWriter.newLine()
        connectionWriter.write(body)
        connectionWriter.newLine()
        connectionWriter.flush()
    }
}