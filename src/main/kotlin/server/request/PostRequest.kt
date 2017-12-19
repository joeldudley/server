package server.request

class PostRequest(
        val body: Map<String, String>, method: String, path: String, protocol: String, headers: Map<String, String>
) : Request(method, path, protocol, headers)