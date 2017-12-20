package server

import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.net.Socket
import kotlin.test.assertEquals

class WriteResponseTests {

    private fun createMockSocket(): Socket {
        val mockSocket = mock(Socket::class.java)

        val mockInputStream = ByteArrayInputStream("".toByteArray())
        `when`(mockSocket.getInputStream()).thenReturn(mockInputStream)

        val mockOutputStream = ByteArrayOutputStream()
        `when`(mockSocket.getOutputStream()).thenReturn(mockOutputStream)

        return mockSocket
    }

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