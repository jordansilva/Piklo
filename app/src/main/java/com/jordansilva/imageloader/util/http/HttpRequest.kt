package com.jordansilva.imageloader.util.http

import java.util.concurrent.TimeUnit

//FIXME: Extract this class to a different package and need to be refactored
data class HttpRequest(var url: String) {
    var requestMethod = "GET"
    var useCaches = true
    var connectionTimeout = TimeUnit.SECONDS.toMillis(10).toInt()
    var readTimeout = TimeUnit.SECONDS.toMillis(10).toInt()
    var allowUserInteraction = false
}

