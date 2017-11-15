package node

import kotlin.concurrent.thread

fun main(args: Array<String>) {
    thread(start = true) {
        NodeA.doStuff()
    }

    thread(start = true) {
        NodeB.doStuff()
    }
}