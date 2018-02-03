package server

private class MyServer(port: Int) : Server(port, listOf(GET_RECEIVED_ROUTE, POST_RECEIVED_ROUTE))

// TODO: Parameterise to not always use localhost.
fun main(args: Array<String>) {
    val port = args[0].toInt()
    val myServer = MyServer(port)
    myServer.start()
}