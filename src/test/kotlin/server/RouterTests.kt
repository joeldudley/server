package server

import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.net.Socket
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RouterTests {
    private val router = Router()

    private fun createMockSocket(inputStreamContents: String): Socket {
        val mockSocket = Mockito.mock(Socket::class.java)

        val mockInputStream = ByteArrayInputStream(inputStreamContents.toByteArray())
        Mockito.`when`(mockSocket.getInputStream()).thenReturn(mockInputStream)

        val mockOutputStream = ByteArrayOutputStream()
        Mockito.`when`(mockSocket.getOutputStream()).thenReturn(mockOutputStream)

        return mockSocket
    }

    @Before
    fun setUp() {
        router.registerRoute("/", server.request.Method.GET, getRootRoute)
        router.registerRoute("/", server.request.Method.POST, postRootRoute)
    }

    @Test
    fun `router dispatches correctly to known GET routes`() {
        val validRequest = "GET / HTTP/1.1\n\n"
        val mockSocket = createMockSocket(validRequest)
        val connection = ClientConnection(mockSocket)
        val request = connection.parseRequest()

        router.handleConnection(request, connection)

        assertEquals(expectedGetRootRouteResponse, mockSocket.getOutputStream().toString())
    }

    @Test
    fun `router dispatches correctly to known POST routes`() {
        val validRequest = "POST / HTTP/1.1\nContent-Length: 0\n\none=two"
        val mockSocket = createMockSocket(validRequest)
        val connection = ClientConnection(mockSocket)
        val request = connection.parseRequest()

        router.handleConnection(request, connection)

        assertEquals(expectedPostRootRouteResponse, mockSocket.getOutputStream().toString())
    }

    @Test
    fun `router throws an exception for unknown routes`() {
        val validRequest = "GET /test HTTP/1.1\n\n"
        val mockSocket = createMockSocket(validRequest)
        val connection = ClientConnection(mockSocket)
        val request = connection.parseRequest()

        assertFailsWith<UnregisteredRouteException> {
            router.handleConnection(request, connection)
        }
    }
}