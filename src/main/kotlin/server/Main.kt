package server

internal val PORT = 4444

fun main(args: Array<String>) {
    Server(PORT).start()
}