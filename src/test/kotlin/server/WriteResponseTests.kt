package server

import org.junit.Test
import kotlin.test.assertEquals

class WriteResponseTests {

    @Test
    fun `response is written correctly`() {
        val body = "Test body"
        val headers = listOf("Content-Type: text/plain", "Content-Length: ${body.length + 1}", "Connection: close")
        val response = Response(StatusLine._200, headers, body)

        val mockSocket = createMockSocket()

        val clientConnection = ClientConnection(mockSocket)
        clientConnection.writeResponse(response)

        val expectedResponse = "${StatusLine._200}\n" + headers.joinToString("\n") + "\n\n" + "$body\n"
        assertEquals(expectedResponse, mockSocket.getOutputStream().toString())
    }
}