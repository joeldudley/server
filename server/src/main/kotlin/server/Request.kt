package server

abstract class Request(val method: Method, val path: String, val protocol: String, val headers: Map<RequestHeader, String>)

enum class Method { GET, POST, PUT, UNKNOWN }

class GetRequest(
        path: String, protocol: String, headers: Map<RequestHeader, String>
) : Request(Method.GET, path, protocol, headers)

class PostRequest(
        val body: Map<String, String>, path: String, protocol: String, headers: Map<RequestHeader, String>
) : Request(Method.POST, path, protocol, headers)

class PutRequest(
        val body: Map<String, String>, path: String, protocol: String, headers: Map<RequestHeader, String>
) : Request(Method.PUT, path, protocol, headers)

class UnknownRequest : Request(Method.UNKNOWN, "", "", mapOf())

enum class RequestHeader { HOST, CONNECTION, CONTENT_LENGTH }