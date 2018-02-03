package server

import org.junit.Test
import server.Method.*
import server.RequestHeader.CONNECTION
import server.RequestHeader.HOST
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ParseRequestTests {

    @Test
    fun `request line is parsed correctly`() {
        val (method, path, protocol) = listOf(GET, "/", "HTTP/1.1")
        val validRequestLine = "$method $path $protocol\n\n"

        val mockSocket = createMockSocket(validRequestLine)

        val connection = Connection(mockSocket)
        val request = connection.parseRequest()
        assertEquals(request.method, method)
        assertEquals(request.path, path)
        assertEquals(request.protocol, protocol)
    }

    @Test
    fun `error is thrown if request line does not contain three elements`() {
        // Does not contain three elements separated by spaces (method, request URI, protocol version).
        val invalidRequestLine = "HTTP /\n\n"

        val mockSocket = createMockSocket(invalidRequestLine)

        val connection = Connection(mockSocket)
        assertFailsWith<MalformedRequestLineException> {
            connection.parseRequest()
        }
    }

    @Test
    fun `method is parsed correctly`() {
        val validGetRequest = "GET / HTTP/1.1\n\n"

        val mockGetSocket = createMockSocket(validGetRequest)

        val getConnection = Connection(mockGetSocket)
        val getRequest = getConnection.parseRequest()
        assert(getRequest.method == GET)

        val validPostRequest = "POST / HTTP/1.1\nContent-Length: 0\n\n"

        val mockPostSocket = createMockSocket(validPostRequest)

        val postConnection = Connection(mockPostSocket)
        val postRequest = postConnection.parseRequest()
        assert(postRequest.method == POST)
    }

    @Test
    fun `method defaults to UNKNOWN if it is not GET, POST or PUT`() {
        val invalidRequest = "PATCH / HTTP/1.1\n\n"

        val mockSocket = createMockSocket(invalidRequest)

        val connection = Connection(mockSocket)
        val request = connection.parseRequest()
        assertEquals(UNKNOWN, request.method)
    }

    @Test
    fun `headers are parsed correctly`() {
        val (hostName, hostValue, connectionName, connectionValue) = listOf("Host", "localhost", "Connection", "Keep-Alive")
        val validRequest = "GET / HTTP/1.1\n$hostName: $hostValue\n$connectionName: $connectionValue\n\n"

        val mockSocket = createMockSocket(validRequest)

        val Connection = Connection(mockSocket)
        val request = Connection.parseRequest()
        val headers = request.headers
        assertEquals(hostValue.toLowerCase(), headers[HOST])
        assertEquals(connectionValue.toLowerCase(), headers[CONNECTION])
    }

    @Test
    fun `error is thrown if headers are missing one or more separating colons`() {
        // Does not contain any semi-colons on at least one line.
        val invalidRequest = "GET / HTTP/1.1\nHost localhost\n\n"

        val mockSocket = createMockSocket(invalidRequest)

        val Connection = Connection(mockSocket)
        assertFailsWith<MissingColonInHeadersException> {
            Connection.parseRequest()
        }
    }

    @Test
    fun `error is thrown if request line and headers are not followed by a blank line`() {
        val invalidRequest1 = "GET / HTTP/1.1\n"
        val invalidRequest2 = "GET / HTTP/1.1\nHost: localhost\n"

        listOf(invalidRequest1, invalidRequest2).forEach { invalidRequest ->
            val mockSocket = createMockSocket(invalidRequest)

            val Connection = Connection(mockSocket)
            assertFailsWith<NoBlankLineAfterHeadersException> {
                Connection.parseRequest()
            }
        }
    }

    @Test
    fun `error is thrown if POST request does not have a Content-Length header`() {
        val invalidRequest = "POST / HTTP/1.1\nHost: localhost\n\none=two\n"

        val mockSocket = createMockSocket(invalidRequest)

        val Connection = Connection(mockSocket)
        assertFailsWith<NoContentLengthHeaderOnPostRequestException> {
            Connection.parseRequest()
        }
    }

    @Test
    fun `POST request body is only parsed to the length of the Content-Length header`() {
        val (name1, param1, name2, param2) = listOf("one", "two", "three", "four")
        val body = "$name1=$param1&$name2=$param2"
        val validRequest = "POST / HTTP/1.1\nContent-Length: ${body.length - 1}\n\n$body\n"

        val mockSocket = createMockSocket(validRequest)

        val Connection = Connection(mockSocket)
        val request = Connection.parseRequest()
        assert(request is PostRequest)
        val shortenedParam2 = param2.subSequence(0..param2.length - 2)
        assertEquals((request as PostRequest).body, mapOf(name1 to param1, name2 to shortenedParam2))
    }

    @Test
    fun `POST request body is parsed correctly`() {
        val (name1, param1, name2, param2) = listOf("one", "two", "three", "four")
        val body = "$name1=$param1&$name2=$param2"
        val validRequest = "POST / HTTP/1.1\nContent-Length: ${body.length}\n\n$body\n"

        val mockSocket = createMockSocket(validRequest)

        val Connection = Connection(mockSocket)
        val request = Connection.parseRequest()
        assert(request is PostRequest)
        assertEquals((request as PostRequest).body, mapOf(name1 to param1, name2 to param2))
    }

    @Test
    fun `error is thrown if body is missing a value`() {
        val body = "one&three=four"
        val invalidRequest = "POST / HTTP/1.1\nContent-Length: ${body.length}\n\n$body\n"

        val mockSocket = createMockSocket(invalidRequest)

        val Connection = Connection(mockSocket)
        assertFailsWith<MissingBodyValueException> {
            Connection.parseRequest()
        }
    }

    @Test
    fun `error is thrown if body is missing a name`() {
        val body = "one=twothree=four"
        val invalidRequest = "POST / HTTP/1.1\nContent-Length: ${body.length}\n\n$body\n"

        val mockSocket = createMockSocket(invalidRequest)

        val Connection = Connection(mockSocket)
        assertFailsWith<MissingBodyNameException> {
            Connection.parseRequest()
        }
    }

    @Test
    fun `error is thrown if body has repeated names`() {
        val body = "one=two&one=three"
        val invalidRequest = "POST / HTTP/1.1\nContent-Length: ${body.length}\n\n$body\n"

        val mockSocket = createMockSocket(invalidRequest)

        val connection = Connection(mockSocket)
        assertFailsWith<RepeatedBodyNameException> {
            connection.parseRequest()
        }
    }

    @Test
    fun `no error is thrown if POST request has a body of length zero`() {
        val validRequest = "POST / HTTP/1.1\nContent-Length: 0\n\n"

        val mockSocket = createMockSocket(validRequest)

        val connection = Connection(mockSocket)
        val request = connection.parseRequest()
        assert(request.method == POST)
        assertEquals((request as PostRequest).body, mapOf())
    }
}