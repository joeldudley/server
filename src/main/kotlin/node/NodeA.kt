package node

fun main(args: Array<String>) {
    NodeA.doStuff()
}

object NodeA {
    fun doStuff() {
        while (true) {
            println("hA")
            Thread.sleep(1_000)
        }
    }
}