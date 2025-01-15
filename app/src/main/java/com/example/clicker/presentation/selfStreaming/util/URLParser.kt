package com.example.clicker.presentation.selfStreaming.util

import java.net.URI
import java.net.URISyntaxException

class UrlParser private constructor(
    uri: URI,
    private val url: String
){



    companion object {
        @Throws(URISyntaxException::class)
        fun parse(endpoint: String, requiredProtocol: Array<String>): UrlParser {
            val uri = URI(endpoint)
            if (uri.scheme != null && !requiredProtocol.contains(uri.scheme.trim())) {
                throw URISyntaxException(endpoint, "Invalid protocol: ${uri.scheme}")
            }
            if (uri.userInfo != null && !uri.userInfo.contains(":")) {
                throw URISyntaxException(endpoint, "Invalid auth. Auth must contain ':'")
            }
            if (uri.host == null) throw URISyntaxException(endpoint, "Invalid host: ${uri.host}")
            if (uri.path == null) throw URISyntaxException(endpoint, "Invalid path: ${uri.host}")
            return UrlParser(uri, endpoint)
        }
    }

    var scheme: String = ""
        private set
    var host: String = ""
        private set
    var port: Int? = null
        private set
    var path: String = ""
        private set
    var query: String? = null
        private set
    var auth: String? = null
        private set

    init {
        val url = uri.toString()
        scheme = uri.scheme
        host = uri.host
        port = if (uri.port < 0) null else uri.port
        path = uri.path.removePrefix("/")
        if (uri.query != null) {
            val i = url.indexOf(uri.query)
            query = url.substring(if (i < 0) 0 else i)
        }
        auth = uri.userInfo
    }

}