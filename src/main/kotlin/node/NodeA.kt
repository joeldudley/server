package node

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket


fun main(args: Array<String>) {
    NodeA.doStuff()
}

object NodeA {
    fun doStuff() {
        // TODO: Make sure everything gets closed.

        val socket = ServerSocket(4444)

        // TODO: Make multithreaded.
        while (true) {
            val connection = socket.accept()
            val inReader = BufferedReader(InputStreamReader(connection.getInputStream()))

            while (true) {
                val line = inReader.readLine() ?: break
                println(line)
            }
        }
    }
}