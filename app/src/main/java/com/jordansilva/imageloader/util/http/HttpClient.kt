package com.jordansilva.imageloader.util.http

import androidx.annotation.WorkerThread
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL

class HttpClient {

    @WorkerThread
    private fun openConnection(request: HttpRequest): HttpURLConnection {
        try {
            val urlConnection = URL(request.url).openConnection() as HttpURLConnection
            urlConnection.run {
                requestMethod = request.requestMethod
                useCaches = request.useCaches
                allowUserInteraction = request.allowUserInteraction
                connectTimeout = request.connectionTimeout
                connect()
            }
            return urlConnection
        } catch (ex: Exception) {
            throw ex
        }
    }

    @WorkerThread
    fun execute(request: HttpRequest): HttpResponse {
        var urlConnection: HttpURLConnection? = null
        return try {
            urlConnection = openConnection(request)

            //Cloning InputStream
            val byteOutputStream = ByteArrayOutputStream()
            urlConnection.inputStream.copyTo(byteOutputStream)
            val inputStream = ByteArrayInputStream(byteOutputStream.toByteArray())

            HttpResponse(
                code = urlConnection.responseCode,
                message = urlConnection.responseMessage,
                source = inputStream
            )
        } catch (ex: Exception) {
            throw ex
        } finally {
            urlConnection?.inputStream?.close()
            urlConnection?.disconnect()
        }
    }

}