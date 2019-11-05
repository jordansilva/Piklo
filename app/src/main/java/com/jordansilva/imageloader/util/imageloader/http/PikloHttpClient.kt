package com.jordansilva.imageloader.util.imageloader.http

import java.io.InputStream

interface PikloHttpClient {
    fun execute(url: String): InputStream?
}
