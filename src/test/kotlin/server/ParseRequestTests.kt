package server

import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.io.ByteArrayInputStream
import java.net.Socket
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import server.Server.GetRequest
import server.Server.PostRequest

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
    fun `error is thrown if request line is malformed`() {
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
    fun `error is thrown if method is not GET or POST`() {
        val invalidRequest = "PUT / HTTP/1.1\r\n\r\n"

        val mockSocket = mock(Socket::class.java)
        val mockInputStream = ByteArrayInputStream(invalidRequest.toByteArray())
        `when`(mockSocket.getInputStream()).thenReturn(mockInputStream)

        assertFailsWith<IllegalArgumentException> {
            server.parseRequest(mockSocket)
        }
    }

    @Test
    fun `method is extracted correctly`() {
        val validGetRequest = "GET / HTTP/1.1\r\n\r\n"

        val mockGetSocket = mock(Socket::class.java)
        val mockGetInputStream = ByteArrayInputStream(validGetRequest.toByteArray())
        `when`(mockGetSocket.getInputStream()).thenReturn(mockGetInputStream)

        val getRequest = server.parseRequest(mockGetSocket)
        assert(getRequest is GetRequest)

        val validPostRequest = "POST / HTTP/1.1\r\n\r\n"

        val mockPostSocket = mock(Socket::class.java)
        val mockPostInputStream = ByteArrayInputStream(validPostRequest.toByteArray())
        `when`(mockPostSocket.getInputStream()).thenReturn(mockPostInputStream)

        val postRequest = server.parseRequest(mockPostSocket)
        assert(postRequest is PostRequest)
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
    fun `error is thrown if headers are malformed`() {
        // Does not contain any semi-colons on at least one line.
        val invalidRequest = "GET / HTTP/1.1\r\nHost localhost\r\n\r\n"

        val mockSocket = mock(Socket::class.java)
        val mockInputStream = ByteArrayInputStream(invalidRequest.toByteArray())
        `when`(mockSocket.getInputStream()).thenReturn(mockInputStream)

        assertFailsWith<IllegalArgumentException> {
            server.parseRequest(mockSocket)
        }
    }

    @Test
    fun `error is thrown if request line and headers are not followed by a blank line`() {
        val invalidRequest1 = "GET / HTTP/1.1\r\n"
        val invalidRequest2 = "GET / HTTP/1.1\r\nHost localhost\r\n"

        for (invalidRequest in listOf(invalidRequest1, invalidRequest2)) {
            val mockSocket = mock(Socket::class.java)
            val mockInputStream = ByteArrayInputStream(invalidRequest.toByteArray())
            `when`(mockSocket.getInputStream()).thenReturn(mockInputStream)

            assertFailsWith<IllegalArgumentException> {
                server.parseRequest(mockSocket)
            }
        }
    }

    @Test
    fun `POST request body is parsed correctly`() {
        val validRequest = "POST / HTTP/1.1\r\nHost: localhost\r\n\r\nsay=Hi&to=Mom"

        val mockSocket = mock(Socket::class.java)
        val mockInputStream = ByteArrayInputStream(validRequest.toByteArray())
        `when`(mockSocket.getInputStream()).thenReturn(mockInputStream)

        val request = server.parseRequest(mockSocket)
        assert(request is PostRequest)
        assertEquals((request as PostRequest).body, mapOf("say" to "Hi", "to" to "Mom"))
    }

    @Test
    fun `no error is thrown if POST request does not have a body`() {
        val validRequest = "POST / HTTP/1.1\r\nHost: localhost\r\n\r\n"

        val mockSocket = mock(Socket::class.java)
        val mockInputStream = ByteArrayInputStream(validRequest.toByteArray())
        `when`(mockSocket.getInputStream()).thenReturn(mockInputStream)

        val request = server.parseRequest(mockSocket)
        assert(request is PostRequest)
        assertEquals((request as PostRequest).body, mapOf())
    }
}