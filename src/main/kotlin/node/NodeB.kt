package node

fun main(args: Array<String>) {
    NodeB.doStuff()
}

object NodeB {
    fun doStuff() {
        while (true) {
            println("hB")
            Thread.sleep(1_000)
        }
    }
}