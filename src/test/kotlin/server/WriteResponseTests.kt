package server

import org.junit.Test
import kotlin.test.assertEquals

class WriteResponseTests {

    @Test
    fun `response is written correctly`() {
        val responseBody = "Test body"
        val responseHeaders = listOf("HTTP/1.1 200 OK", "Content-Type: text/plain", "Content-Length: ${responseBody.length}", "Connection: close")
        val expectedResponse = responseHeaders.joinToString("\n") + "\n\n" + "$responseBody\n"

        val mockSocket = createMockSocket()

        val clientConnection = ClientConnection(mockSocket)
        clientConnection.writeResponse(responseHeaders, responseBody)

        assertEquals(expectedResponse, mockSocket.getOutputStream().toString())
    }
}