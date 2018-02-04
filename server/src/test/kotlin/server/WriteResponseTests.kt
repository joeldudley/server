package server

import org.junit.Test
import server.ResponseHeader.*
import kotlin.test.assertEquals

class WriteResponseTests {

    @Test
    fun `response is written correctly`() {
        val body = "Test body"
        val response = Response(body)

        val mockSocket = createMockSocket()
        val connection = Connection(mockSocket)
        connection.writeResponse(response)
        
        assertEquals(response.toString(), mockSocket.getOutputStream().toString())
    }
}