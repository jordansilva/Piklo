package com.jordansilva.imageloader.util.http

import java.util.concurrent.TimeUnit

data class HttpRequest(var url: String) {
    var requestMethod = "GET"
    var useCaches = true
    var connectionTimeout = TimeUnit.SECONDS.toMillis(10).toInt()
    var readTimeout = TimeUnit.SECONDS.toMillis(10).toInt()
    var allowUserInteraction = false
}

