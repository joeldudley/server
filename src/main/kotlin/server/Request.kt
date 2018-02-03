package server

abstract class Request(val method: Method, val path: String, val protocol: String, val headers: Map<String, String>)

enum class Method { GET, POST }

class GetRequest(
        method: Method, path: String, protocol: String, headers: Map<String, String>
) : Request(method, path, protocol, headers)

class PostRequest(
        val body: Map<String, String>, method: Method, path: String, protocol: String, headers: Map<String, String>
) : Request(method, path, protocol, headers)