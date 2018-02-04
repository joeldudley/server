package server

import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import server.Method.GET
import server.Method.POST
import java.net.ConnectException
import java.net.URL
import kotlin.concurrent.thread
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val PORT = 4444

// TODO: Tests of the server receiving junk and responding gracefully.
// TODO: Tests of server reusing threads.
// TODO: Check headers here?
class IntegrationTests {
    private lateinit var server: Server
    private val client = OkHttpClient()

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
        val url = URL("http://localhost:$PORT\n")
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        assert(response.isSuccessful)
        assertEquals("GET received\n", response.body()?.string())
    }

    @Test
    fun `server responds to POST requests`() {
        val url = URL("http://localhost:$PORT\n")
        val mediaType = MediaType.parse("application/json")
        val body = RequestBody.create(mediaType, "one=two&three=four")
        val request = Request.Builder().url(url).post(body).build()
        val response = client.newCall(request).execute()
        assert(response.isSuccessful)
        assertEquals("POST received\n", response.body()?.string())
    }

    @Test
    fun `server rejects connections on other ports`() {
        val url = URL("http://localhost:${PORT + 1}\n")
        val request = Request.Builder().url(url).build()

        assertFailsWith<ConnectException> {
            client.newCall(request).execute()
        }
    }

    @Test
    fun `server throws 404 exceptions for unregistered paths`() {
        val url = URL("http://localhost:$PORT/test\n")
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        assert(!response.isSuccessful)
        assertEquals("Not found.\n", response.body()?.string())
    }

    @Test
    fun `server throws 405 exceptions for unallowed methods`() {
        val url = URL("http://localhost:$PORT\n")
        val mediaType = MediaType.parse("application/json")
        val body = RequestBody.create(mediaType, "one=two&three=four")
        val request = Request.Builder().url(url).put(body).build()
        val response = client.newCall(request).execute()
        assert(!response.isSuccessful)
        assertEquals("Method not allowed.\n", response.body()?.string())
    }

    @Test
    fun `server throws 405 exceptions for unrecognised methods`() {
        val url = URL("http://localhost:$PORT\n")
        val request = Request.Builder().url(url).method("XYZ", null).build()
        val response = client.newCall(request).execute()
        assert(!response.isSuccessful)
        assertEquals("Method not allowed.\n", response.body()?.string())
    }

    @Test
    fun `server has a shutdown endpoint`() {
        val shutdownUrl = URL("http://localhost:$PORT/shutdown\n")
        val shutdownRequest = Request.Builder().url(shutdownUrl).get().build()
        val shutdownResponse = client.newCall(shutdownRequest).execute()
        assert(shutdownResponse.isSuccessful)
        assertEquals("Server shut down.\n", shutdownResponse.body()?.string())

        val failedUrl = URL("http://localhost:${PORT + 1}\n")
        val failedRequest = Request.Builder().url(failedUrl).build()

        assertFailsWith<ConnectException> {
            client.newCall(failedRequest).execute()
        }
    }
}