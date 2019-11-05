package com.jordansilva.imageloader.repository.http

data class HttpRequest(var url: String) {
    var requestMethod = "GET"
    var useCaches = true
    var connectionTimeout = 10 * 1000
    var readTimeout = 10 * 1000
}

