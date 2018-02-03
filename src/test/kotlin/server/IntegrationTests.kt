package server

import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import server.request.Method.GET
import server.request.Method.POST
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.URL
import kotlin.concurrent.thread
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val PORT = 4444

// TODO: Tests of the server receiving junk and responding gracefully.
// TODO: Tests of server reusing threads.
class IntegrationTests {
    private lateinit var server: Server

    @Before
    fun setUp() {
        server = object: Server(PORT, listOf(Route("/", GET, getRootHandler), Route("/", POST, postRootHandler))) {}
        // The server needs to run on a separate thread, or there won't be a thread for the tests.
        thread(start = true) {
            server.start()
        }
    }

    @After
    fun shutDown() {
        server.shutDown()
    }

    @Test
    fun `server responds to GET requests`() {
        val client = OkHttpClient()
        val url = URL("http://localhost:$PORT\n")
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        assert(response.isSuccessful)
        assertEquals(response.body()?.string(), "GET received\n")
    }

    @Test
    fun `server responds to POST requests`() {
        val client = OkHttpClient()
        val url = URL("http://localhost:$PORT\n")
        val mediaType = MediaType.parse("application/json")
        val body = RequestBody.create(mediaType, "one=two&three=four")
        val request = Request.Builder().url(url).post(body).build()
        val response = client.newCall(request).execute()
        assert(response.isSuccessful)
        assertEquals(response.body()?.string(), "POST received\n")
    }

    @Test
    fun `server rejects connections on other ports`() {
        val client = OkHttpClient()
        val url = URL("http://localhost:${PORT + 1}\n")
        val request = Request.Builder().url(url).build()

        assertFailsWith<ConnectException> {
            client.newCall(request).execute()
        }
    }

    @Test
    fun `server rejects connections on unregistered routes`() {
        val client = OkHttpClient()
        val url = URL("http://localhost:$PORT/test\n")
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        assert(!response.isSuccessful)
        assertEquals(response.body()?.string(), "Unrecognised route\n")
    }
}