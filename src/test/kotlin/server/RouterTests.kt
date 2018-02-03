package server

import org.junit.Test
import server.Method.GET
import server.Method.POST
import kotlin.test.assertEquals

class RouterTests {
    // This router configuration implicitly tests having multiple methods for the same path.
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
    fun `router throws 404 exceptions for unregistered paths`() {
        val invalidRequest = "GET /test HTTP/1.1\n\n"
        val mockSocket = createMockSocket(invalidRequest)
        val connection = ClientConnection(mockSocket)
        val request = connection.parseRequest()

        val response = router.handleConnection(request)

        assertEquals(StatusLine._404, response.statusLine)
        assertEquals(_404HandlerHeaders, response.headers)
        assertEquals(_404HandlerBody, response.body)
    }

    @Test
    fun `router throws 405 exceptions for unallowed methods`() {
        val invalidRequest = "PUT / HTTP/1.1\nContent-Length: 0\n\none=two"
        val mockSocket = createMockSocket(invalidRequest)
        val connection = ClientConnection(mockSocket)
        val request = connection.parseRequest()

        val response = router.handleConnection(request)

        assertEquals(StatusLine._405, response.statusLine)
        assertEquals(_405HandlerHeaders, response.headers)
        assertEquals(_405HandlerBody, response.body)
    }

    // TODO: test for unrecognised verb
}