package com.jordansilva.imageloader.repository.http

interface HttpClient {
    fun execute(request: HttpRequest): HttpResponse
}

