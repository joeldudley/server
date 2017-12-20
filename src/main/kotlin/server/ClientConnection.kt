package server

import server.request.GetRequest
import server.request.Method.GET
import server.request.Method.POST
import server.request.PostRequest
import server.request.Request
import java.net.Socket

class ClientConnection(connection: Socket) {
    private val connectionReader = connection.getInputStream().bufferedReader()
    private val connectionWriter = connection.getOutputStream().bufferedWriter()

    internal fun parseRequest(): Request {
        val (method, path, protocol) = extractRequestLine()
        val headers = extractHeaders()
        return when (method) {
            "GET" -> GetRequest(GET, path, protocol, headers)
            "POST" -> {
                val contentLengthString = headers["content-length"]
                        ?: throw NoContentLengthHeaderOnPostRequestException()
                val contentLength = contentLengthString.toInt()
                val body = extractBody(contentLength)
                PostRequest(body, POST, path, protocol, headers)
            }
            else -> throw UnrecognisedHTTPMethodException()
        }
    }

    private fun extractRequestLine(): Triple<String, String, String> {
        val requestLine = connectionReader.readLine()
        val requestLineRegex = Regex("""[^ ]+""")
        val requestLineMatchResults = requestLineRegex.findAll(requestLine)
        val requestLineItems = requestLineMatchResults.map { it.value }.toList()
        if (requestLineItems.size != 3) {
            throw MalformedRequestLineException()
        }
        val (method, path, protocol) = requestLineItems
        return Triple(method, path, protocol)
    }

    private fun extractHeaders(): Map<String, String> {
        val headers = mutableMapOf<String, String>()

        while (true) {
            val line = connectionReader.readLine() ?:
                    throw NoBlankLineAfterHeadersException()

            if (line == "") break

            if (!line.contains(':')) throw MissingColonInHeadersException()

            val (header, value) = line.toLowerCase().split(':', limit = 2).map { it.trim() }
            headers.put(header, value)
        }

        return headers
    }

    private fun extractBody(contentLength: Int): Map<String, String> {
        val bodyChars = CharArray(contentLength)
        connectionReader.read(bodyChars, 0, contentLength)
        val bodyString = bodyChars.joinToString("")

        val body = mutableMapOf<String, String>()

        // TODO: Do this more gracefully - regex below?
        if (contentLength == 0) {
            return body
        }

        val namesAndValues = bodyString.split('&')
            for (nameAndValue in namesAndValues) {
                val numberOfSeparators = nameAndValue.count { it == '=' }
                if (numberOfSeparators == 0) throw MissingBodyValueException()
                if (numberOfSeparators >= 2) throw MissingBodyNameException()

                val (name, value) = nameAndValue.split('=', limit = 2).map { it.trim() }

                if (name in body) throw RepeatedBodyNameException()

                body.put(name, value)
            }

        return body
    }

    internal fun writeResponse(headers: List<String>, body: String) {
        for (header in headers) {
            connectionWriter.write(header)
            connectionWriter.newLine()
        }
        connectionWriter.newLine()
        connectionWriter.write(body)
        connectionWriter.newLine()
        connectionWriter.flush()
    }
}

class NoContentLengthHeaderOnPostRequestException: IllegalArgumentException()
class UnrecognisedHTTPMethodException: IllegalArgumentException()
class MalformedRequestLineException: IllegalArgumentException()
class MissingBodyNameException: IllegalArgumentException()
class MissingBodyValueException: IllegalArgumentException()
class MissingColonInHeadersException: IllegalArgumentException()
class NoBlankLineAfterHeadersException: IllegalArgumentException()
class RepeatedBodyNameException: IllegalArgumentException()