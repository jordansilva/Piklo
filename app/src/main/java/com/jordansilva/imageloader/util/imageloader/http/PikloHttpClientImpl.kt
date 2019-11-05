package com.jordansilva.imageloader.util.imageloader.http

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class PikloHttpClientImpl : PikloHttpClient {

    private companion object {
        const val REQUEST_METHOD = "GET"
        const val CONNECTION_TIMEOUT = 10 * 1000
        const val READ_TIMEOUT = 15 * 1000
        const val USE_CACHE = true
    }

    override fun execute(url: String): InputStream? {
        var connection: HttpURLConnection? = null
        try {
            connection = URL(url).openConnection() as HttpURLConnection
            connection.run {
                requestMethod = REQUEST_METHOD
                useCaches = USE_CACHE
                connectTimeout = CONNECTION_TIMEOUT
                readTimeout = READ_TIMEOUT
                connect()
            }

            return if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val byteOutputStream = ByteArrayOutputStream()
                connection.inputStream.copyTo(byteOutputStream)
                return ByteArrayInputStream(byteOutputStream.toByteArray())
            } else {
                null
            }
        } catch (ex: Exception) {
            throw ex
        } finally {
            connection?.inputStream?.close()
            connection?.disconnect()
        }
    }
}
