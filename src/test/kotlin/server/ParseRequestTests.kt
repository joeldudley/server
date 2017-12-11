package server

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val PORT = 4444

class ParseRequestTests {
    private val server = Server(PORT)

    @Test
    fun `request line is parsed correctly`() {
        val (method, path, protocol) = listOf("GET", "/", "HTTP/1.1")
        val validRequestLine = "$method $path $protocol\r\n\r\n"
        val bufferedReader = validRequestLine.byteInputStream().bufferedReader()

        val request = server.parseRequestFromConnectionReader(bufferedReader)
        assertEquals(request.method, method)
        assertEquals(request.path, path)
        assertEquals(request.protocol, protocol)
    }

    @Test
    fun `request line must contain three elements separated by spaces`() {
        // Does not contain three elements separated by spaces (method, request URI, protocol version).
        val invalidRequestLine = "HTTP /\r\n\r\n"
        val bufferedReader = invalidRequestLine.byteInputStream().bufferedReader()

        assertFailsWith<IllegalArgumentException> {
            server.parseRequestFromConnectionReader(bufferedReader)
        }
    }

    @Test
    fun `headers are parsed correctly`() {
        val (host, connection) = listOf("Host" to "localhost", "Connection" to "Keep-Alive")
        val validRequestLine = "GET / HTTP/1.1\r\n${host.first}: ${host.second}\r\n${connection.first}: ${connection.second}\r\n\r\n"
        val bufferedReader = validRequestLine.byteInputStream().bufferedReader()

        val request = server.parseRequestFromConnectionReader(bufferedReader)
        val headers = request.headers
        assertEquals(headers[host.first], host.second)
        assertEquals(headers[connection.first], connection.second)
    }

    @Test
    fun `headers must be formatted correctly`() {
        // Does not contain any semi-colons on at least one line.
        val invalidHeaders1 = "GET / HTTP/1.1\r\nHost localhost\r\n\r\n"
        // Is not followed by a blank line.
        val invalidHeaders2 = "GET / HTTP/1.1\r\nHost: localhost: localhost\r\n"

        for (invalidHeaders in listOf(invalidHeaders1, invalidHeaders2)) {
            val bufferedReader = invalidHeaders.byteInputStream().bufferedReader()

            assertFailsWith<IllegalArgumentException> {
                server.parseRequestFromConnectionReader(bufferedReader)
            }
        }
    }
}