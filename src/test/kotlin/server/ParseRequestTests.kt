package server

import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import server.request.GetRequest
import server.request.PostRequest
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.net.Socket
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ParseRequestTests {

    fun createMockSocket(inputStreamContents: String): Socket {
        val mockSocket = mock(Socket::class.java)

        val mockInputStream = ByteArrayInputStream(inputStreamContents.toByteArray())
        `when`(mockSocket.getInputStream()).thenReturn(mockInputStream)

        val mockOutputStream = ByteArrayOutputStream()
        `when`(mockSocket.getOutputStream()).thenReturn(mockOutputStream)

        return mockSocket
    }

    @Test
    fun `request line is parsed correctly`() {
        val (method, path, protocol) = listOf("GET", "/", "HTTP/1.1")
        val validRequestLine = "$method $path $protocol\r\n\r\n"

        val mockSocket = createMockSocket(validRequestLine)

        val clientConnection = ClientConnection(mockSocket)
        val request = clientConnection.parseRequest()
        assertEquals(request.method, method)
        assertEquals(request.path, path)
        assertEquals(request.protocol, protocol)
    }

    @Test
    fun `error is thrown if request line is malformed`() {
        // Does not contain three elements separated by spaces (method, request URI, protocol version).
        val invalidRequestLine = "HTTP /\r\n\r\n"

        val mockSocket = createMockSocket(invalidRequestLine)

        val clientConnection = ClientConnection(mockSocket)
        assertFailsWith<IllegalArgumentException> {
            clientConnection.parseRequest()
        }
    }

    @Test
    fun `error is thrown if method is not GET or POST`() {
        val invalidRequest = "PUT / HTTP/1.1\r\n\r\n"

        val mockSocket = createMockSocket(invalidRequest)

        val clientConnection = ClientConnection(mockSocket)
        assertFailsWith<IllegalArgumentException> {
            clientConnection.parseRequest()
        }
    }

    @Test
    fun `method is extracted correctly`() {
        val validGetRequest = "GET / HTTP/1.1\r\n\r\n"

        val mockGetSocket = createMockSocket(validGetRequest)

        val clientGetConnection = ClientConnection(mockGetSocket)
        val getRequest = clientGetConnection.parseRequest()
        assert(getRequest is GetRequest)

        val validPostRequest = "POST / HTTP/1.1\r\n\r\n"

        val mockPostSocket = createMockSocket(validPostRequest)

        val clientPostConnection = ClientConnection(mockPostSocket)
        val postRequest = clientPostConnection.parseRequest()
        assert(postRequest is PostRequest)
    }

    @Test
    fun `headers are parsed correctly`() {
        val (host, connection) = listOf("Host" to "localhost", "Connection" to "Keep-Alive")
        val validRequest = "GET / HTTP/1.1\r\n${host.first}: ${host.second}\r\n${connection.first}: ${connection.second}\r\n\r\n"

        val mockSocket = createMockSocket(validRequest)

        val clientConnection = ClientConnection(mockSocket)
        val request = clientConnection.parseRequest()
        val headers = request.headers
        assertEquals(headers[host.first], host.second)
        assertEquals(headers[connection.first], connection.second)
    }

    @Test
    fun `error is thrown if headers are malformed`() {
        // Does not contain any semi-colons on at least one line.
        val invalidRequest = "GET / HTTP/1.1\r\nHost localhost\r\n\r\n"

        val mockSocket = createMockSocket(invalidRequest)

        val clientConnection = ClientConnection(mockSocket)
        assertFailsWith<IllegalArgumentException> {
            clientConnection.parseRequest()
        }
    }

    @Test
    fun `error is thrown if request line and headers are not followed by a blank line`() {
        val invalidRequest1 = "GET / HTTP/1.1\r\n"
        val invalidRequest2 = "GET / HTTP/1.1\r\nHost localhost\r\n"

        for (invalidRequest in listOf(invalidRequest1, invalidRequest2)) {
            val mockSocket = createMockSocket(invalidRequest)

            val clientConnection = ClientConnection(mockSocket)
            assertFailsWith<IllegalArgumentException> {
                clientConnection.parseRequest()
            }
        }
    }

    @Test
    fun `POST request body is parsed correctly`() {
        val validRequest = "POST / HTTP/1.1\r\nHost: localhost\r\n\r\none=two&three=four\r\n"

        val mockSocket = createMockSocket(validRequest)

        val clientConnection = ClientConnection(mockSocket)
        val request = clientConnection.parseRequest()
        assert(request is PostRequest)
        assertEquals((request as PostRequest).body, mapOf("one" to "two", "three" to "four"))
    }

    @Test
    fun `error is thrown if body is malformed`() {
        val invalidRequest1 = "POST / HTTP/1.1\r\nHost: localhost\r\n\r\none&three=four\r\n"
        val invalidRequest2 = "POST / HTTP/1.1\r\nHost: localhost\r\n\r\none=twothree=four\r\n"

        for (invalidRequest in listOf(invalidRequest1, invalidRequest2)) {
            val mockSocket = createMockSocket(invalidRequest)

            val clientConnection = ClientConnection(mockSocket)
            assertFailsWith<IllegalArgumentException> {
                clientConnection.parseRequest()
            }
        }
    }

    @Test
    fun `error is thrown if body has repeated names`() {
        val invalidRequest = "POST / HTTP/1.1\r\nHost: localhost\r\n\r\none=two&one=three\r\n"

        val mockSocket = createMockSocket(invalidRequest)

        val clientConnection = ClientConnection(mockSocket)
        assertFailsWith<IllegalArgumentException> {
            clientConnection.parseRequest()
        }
    }

    @Test
    fun `no error is thrown if POST request does not have a body`() {
        val validRequest = "POST / HTTP/1.1\r\nHost: localhost\r\n\r\n"

        val mockSocket = createMockSocket(validRequest)

        val clientConnection = ClientConnection(mockSocket)
        val request = clientConnection.parseRequest()
        assert(request is PostRequest)
        assertEquals((request as PostRequest).body, mapOf())
    }
}