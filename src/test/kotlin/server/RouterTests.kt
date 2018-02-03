package server

import org.junit.Test
import server.request.Method.GET
import server.request.Method.POST
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RouterTests {
    private val router = Router(listOf(Route("/", GET, getRootHandler), Route("/", POST, postRootHandler)))

    @Test
    fun `router dispatches correctly to known GET routes`() {
        val validRequest = "GET / HTTP/1.1\n\n"
        val mockSocket = createMockSocket(validRequest)
        val connection = ClientConnection(mockSocket)
        val request = connection.parseRequest()

        val (headers, body) = router.handleConnection(request)

        assertEquals(expectedGetRootHandlerHeaders, headers)
        assertEquals(expectedGetRootHandlerBody, body)
    }

    @Test
    fun `router dispatches correctly to known POST routes`() {
        val validRequest = "POST / HTTP/1.1\nContent-Length: 0\n\none=two"
        val mockSocket = createMockSocket(validRequest)
        val connection = ClientConnection(mockSocket)
        val request = connection.parseRequest()

        val (headers, body) = router.handleConnection(request)

        assertEquals(expectedPostRootHandlerHeaders, headers)
        assertEquals(expectedPostRootHandlerBody, body)
    }

    @Test
    fun `router throws an exception for unknown routes`() {
        val validRequest = "GET /test HTTP/1.1\n\n"
        val mockSocket = createMockSocket(validRequest)
        val connection = ClientConnection(mockSocket)
        val request = connection.parseRequest()

        assertFailsWith<UnregisteredRouteException> {
            router.handleConnection(request)
        }
    }
}