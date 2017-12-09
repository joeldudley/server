package server

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val PORT = 4444

class ExtractRequestLineAndHeadersTests {
    private val server = Server(PORT)

    @Test
    fun `request line is parsed correctly`() {
        val (method, path, protocol) = listOf("GET", "/", "HTTP/1.1")
        val validRequestLine = "$method $path $protocol\r\n"
        val bufferedReader = validRequestLine.byteInputStream().bufferedReader()

        val requestLine = server.extractRequestLine(bufferedReader)
        assertEquals(requestLine.method, method)
        assertEquals(requestLine.path, path)
        assertEquals(requestLine.protocol, protocol)
    }

    @Test
    fun `request line must be formatted correctly`() {
        // Does not contain three elements separated by spaces (method, request URI, protocol version).
        val invalidRequestLine = "HTTP /\r\n"
        val bufferedReader = invalidRequestLine.byteInputStream().bufferedReader()

        assertFailsWith<IllegalArgumentException> {
            server.extractRequestLine(bufferedReader)
        }
    }

    @Test
    fun `headers are parsed correctly`() {
        val (host, connection) = listOf("Host" to "localhost", "Connection" to "Keep-Alive")
        val validRequestLine = "GET / HTTP/1.1\r\n${host.first}: ${host.second}\r\n${connection.first}: ${connection.second}\r\n\r\n"
        val bufferedReader = validRequestLine.byteInputStream().bufferedReader()

        server.extractRequestLine(bufferedReader)
        val headerMap = server.extractHeaders(bufferedReader)
        assertEquals(headerMap[host.first], host.second)
        assertEquals(headerMap[connection.first], connection.second)
    }

    @Test
    fun `headers must be formatted correctly`() {
        // Does not contain any semi-colons on at least one line.
        val invalidHeaders1 = "GET / HTTP/1.1\r\nHost localhost\r\n\r\n"
        // Contains two or more semi-colons on at least one line.
        val invalidHeaders2 = "GET / HTTP/1.1\r\nHost: localhost: localhost\r\n\r\n"
        // Is not followed by a blank line.
        val invalidHeaders3 = "GET / HTTP/1.1\r\nHost: localhost: localhost\r\n"

        for (invalidHeaders in listOf(invalidHeaders1, invalidHeaders2, invalidHeaders3)) {
            val bufferedReader = invalidHeaders.byteInputStream().bufferedReader()

            assertFailsWith<IllegalArgumentException> {
                server.extractRequestLine(bufferedReader)
                server.extractHeaders(bufferedReader)
            }
        }
    }
}