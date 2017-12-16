package server

import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.io.ByteArrayInputStream
import java.net.Socket
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ParseRequestTests {
    private val server = Server(PORT)

    @Test
    fun `request line is parsed correctly`() {
        val (method, path, protocol) = listOf("GET", "/", "HTTP/1.1")
        val validRequestLine = "$method $path $protocol\r\n\r\n"

        val mockSocket = mock(Socket::class.java)
        val mockInputStream = ByteArrayInputStream(validRequestLine.toByteArray())
        `when`(mockSocket.getInputStream()).thenReturn(mockInputStream)

        val request = server.parseRequest(mockSocket)
        assertEquals(request.method, method)
        assertEquals(request.path, path)
        assertEquals(request.protocol, protocol)
    }

    @Test
    fun `request line contains three elements separated by spaces`() {
        // Does not contain three elements separated by spaces (method, request URI, protocol version).
        val invalidRequestLine = "HTTP /\r\n\r\n"

        val mockSocket = mock(Socket::class.java)
        val mockInputStream = ByteArrayInputStream(invalidRequestLine.toByteArray())
        `when`(mockSocket.getInputStream()).thenReturn(mockInputStream)

        assertFailsWith<IllegalArgumentException> {
            server.parseRequest(mockSocket)
        }
    }

    @Test
    fun `headers are parsed correctly`() {
        val (host, connection) = listOf("Host" to "localhost", "Connection" to "Keep-Alive")
        val validRequest = "GET / HTTP/1.1\r\n${host.first}: ${host.second}\r\n${connection.first}: ${connection.second}\r\n\r\n"

        val mockSocket = mock(Socket::class.java)
        val mockInputStream = ByteArrayInputStream(validRequest.toByteArray())
        `when`(mockSocket.getInputStream()).thenReturn(mockInputStream)

        val request = server.parseRequest(mockSocket)
        val headers = request.headers
        assertEquals(headers[host.first], host.second)
        assertEquals(headers[connection.first], connection.second)
    }

    @Test
    fun `headers are formatted correctly`() {
        // Does not contain any semi-colons on at least one line.
        val invalidRequest1 = "GET / HTTP/1.1\r\nHost localhost\r\n\r\n"
        // Is not followed by a blank line.
        val invalidRequest2 = "GET / HTTP/1.1\r\nHost: localhost: localhost\r\n"

        for (invalidRequest in listOf(invalidRequest1, invalidRequest2)) {
            val mockSocket = mock(Socket::class.java)
            val mockInputStream = ByteArrayInputStream(invalidRequest.toByteArray())
            `when`(mockSocket.getInputStream()).thenReturn(mockInputStream)

            assertFailsWith<IllegalArgumentException> {
                server.parseRequest(mockSocket)
            }
        }
    }
}