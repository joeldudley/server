package server.request

class GetRequest(
        method: String, path: String, protocol: String, headers: Map<String, String>
) : Request(method, path, protocol, headers)
