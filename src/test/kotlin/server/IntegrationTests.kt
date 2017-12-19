package server

import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.net.ConnectException
import java.net.URL
import kotlin.concurrent.thread
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

// TODO: Tests of the server receiving junk and responding gracefully.

class IntegrationTests {
    private lateinit var server: Server

    @Before
    fun setUp() {
        server = Server(PORT)
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
        val url = URL("http://localhost:$PORT\r\n")
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        assert(response.isSuccessful)
        assertEquals(response.body()?.string(), "GET received\n")
    }

    @Test
    fun `server responds to POST requests`() {
        val client = OkHttpClient()
        val url = URL("http://localhost:$PORT\r\n")
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
        val url = URL("http://localhost:${PORT + 1}\r\n")
        val request = Request.Builder().url(url).build()

        assertFailsWith<ConnectException> {
            client.newCall(request).execute()
        }
    }

    @Test
    fun `the server reuses threads`() {
        // TODO: Need to send the server some endless piece of work somehow.
    }
}