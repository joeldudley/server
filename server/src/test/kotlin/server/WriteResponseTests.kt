package server

import org.junit.Test
import server.ResponseHeader.*
import kotlin.test.assertEquals

class WriteResponseTests {

    @Test
    fun `response is written correctly`() {
        val body = "Test body"
        val headers = listOf(ContentType("text/plain"), ContentLength(body.length + 1), Connection("close"))
        val response = Response(StatusLine._200, headers, body)

        val mockSocket = createMockSocket()

        val connection = Connection(mockSocket)
        connection.writeResponse(response)

        val expectedResponse = "${StatusLine._200}\n" + headers.joinToString("\n") + "\n\n" + "$body\n"
        assertEquals(expectedResponse, mockSocket.getOutputStream().toString())
    }
}