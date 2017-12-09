package server

import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import java.net.ConnectException
import java.net.URL
import kotlin.concurrent.thread
import kotlin.test.assertFailsWith

private val PORT = 4444

class ServerTests {
    @Before
    fun setUp() {
        thread(start = true) {
            val server = Server(PORT)
            server.start()
        }
    }

    @Test
    fun `server accepts connections on chosen port`() {
        val url = URL("http://localhost:$PORT")
        val connection = url.openConnection()
        connection.connect()
    }

    @Test
    fun `server rejects connections on other ports`() {
        val url = URL("http://localhost:${PORT + 1}")
        val connection = url.openConnection()

        assertFailsWith<ConnectException> {
            connection.connect()
        }
    }

    @Test
    fun `the server reuses threads`() {
        val url = URL("http://localhost:$PORT")

        for (i in 0..100) {
            val connection = url.openConnection()
            val connectionReader = connection.getInputStream().bufferedReader()

            while (true) {
                val line = connectionReader.readLine() ?: break
                println(line)
            }
        }
    }
}