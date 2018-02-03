package server

import org.junit.Test
import server.Method.GET
import server.Method.POST
import kotlin.test.assertEquals

class RouterTests {
    private val router = Router(listOf(Route("/", GET, getRootHandler), Route("/", POST, postRootHandler)))

    @Test
    fun `router dispatches correctly to known GET routes`() {
        val validRequest = "GET / HTTP/1.1\n\n"
        val mockSocket = createMockSocket(validRequest)
        val connection = ClientConnection(mockSocket)
        val request = connection.parseRequest()

        val response = router.handleConnection(request)

        assertEquals(StatusLine._200, response.statusLine)
        assertEquals(expectedGetRootHandlerHeaders, response.headers)
        assertEquals(expectedGetRootHandlerBody, response.body)
    }

    @Test
    fun `router dispatches correctly to known POST routes`() {
        val validRequest = "POST / HTTP/1.1\nContent-Length: 0\n\none=two"
        val mockSocket = createMockSocket(validRequest)
        val connection = ClientConnection(mockSocket)
        val request = connection.parseRequest()

        val response = router.handleConnection(request)

        assertEquals(StatusLine._200, response.statusLine)
        assertEquals(expectedPostRootHandlerHeaders, response.headers)
        assertEquals(expectedPostRootHandlerBody, response.body)
    }

    @Test
    fun `router throws an exception for unknown routes`() {
        val validRequest = "GET /test HTTP/1.1\n\n"
        val mockSocket = createMockSocket(validRequest)
        val connection = ClientConnection(mockSocket)
        val request = connection.parseRequest()

        val response= router.handleConnection(request)

        assertEquals(StatusLine._500, response.statusLine)
        assertEquals(expectedUnrecognisedRouteHandlerHeaders, response.headers)
        assertEquals(expectedUnrecognisedRouteHandlerBody, response.body)
    }
}