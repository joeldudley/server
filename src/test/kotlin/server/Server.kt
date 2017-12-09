package server

import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.Before
import org.junit.Test
import java.net.ConnectException
import java.net.URL
import kotlin.concurrent.thread
import kotlin.test.assertFailsWith

private val PORT = 4444

class ServerTests {
    @Before
    fun setUp() {
        val server = Server(PORT)
        thread(start = true) {
            server.start()
        }
    }

    @Test
    fun `server accepts connections on chosen port`() {
        val client = OkHttpClient()
        val url = URL("http://localhost:$PORT\r\n")
        val request = Request.Builder().url(url).build()
        client.newCall(request).execute()
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
        val client = OkHttpClient()
        val url = URL("http://localhost:$PORT\r\n")
        val request = Request.Builder().url(url).build()

        for (i in 0..100) {
            client.newCall(request).execute()
        }
    }
}