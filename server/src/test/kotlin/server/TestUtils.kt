package server

import org.mockito.Mockito
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.net.Socket

internal fun createMockSocket(inputStreamContents: String = ""): Socket {
    val mockSocket = Mockito.mock(Socket::class.java)

    val mockInputStream = ByteArrayInputStream(inputStreamContents.toByteArray())
    Mockito.`when`(mockSocket.getInputStream()).thenReturn(mockInputStream)

    val mockOutputStream = ByteArrayOutputStream()
    Mockito.`when`(mockSocket.getOutputStream()).thenReturn(mockOutputStream)

    return mockSocket
}