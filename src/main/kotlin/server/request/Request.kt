package server.request

abstract class Request(val method: String, val path: String, val protocol: String, val headers: Map<String, String>)