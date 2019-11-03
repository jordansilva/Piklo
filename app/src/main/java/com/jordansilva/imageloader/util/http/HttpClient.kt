package com.jordansilva.imageloader.util.http

import android.util.Log
import androidx.annotation.WorkerThread
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL

class HttpClient {

    @WorkerThread
    private fun openConnection(request: HttpRequest, maxRedirect: Int = 3): HttpURLConnection {
        try {
            val urlConnection = URL(request.url).openConnection() as HttpURLConnection
            urlConnection.apply {
                requestMethod = request.requestMethod
                useCaches = request.useCaches
                allowUserInteraction = request.allowUserInteraction
                connectTimeout = request.connectionTimeout
            }
            HttpURLConnection.setFollowRedirects(true)
            urlConnection.connect()

            return when (urlConnection.responseCode) {
                HttpURLConnection.HTTP_MOVED_PERM,
                HttpURLConnection.HTTP_MOVED_TEMP,
                HttpURLConnection.HTTP_SEE_OTHER -> {
                    return if (maxRedirect > 0) {
                        val location = urlConnection.getHeaderField("Location")
                        request.url = location
                        openConnection(request, maxRedirect - 1)
                    } else {
                        urlConnection
                    }
                }
                else -> urlConnection
            }
        } catch (ex: Exception) {
            throw ex
        }
    }

    @WorkerThread
    fun execute(request: HttpRequest): HttpResponse {
        var urlConnection: HttpURLConnection? = null
        try {
            urlConnection = openConnection(request)

            //Cloning InputStream
            val byteOutputStream = ByteArrayOutputStream()
            urlConnection.inputStream.copyTo(byteOutputStream)
            val inputStream = ByteArrayInputStream(byteOutputStream.toByteArray())

            return HttpResponse(
                urlConnection.responseCode,
                urlConnection.responseMessage,
                inputStream
            )
        } catch (ex: Exception) {
            throw ex
        } finally {
            urlConnection?.disconnect()
        }
    }

}