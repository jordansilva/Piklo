package com.jordansilva.imageloader.util

import androidx.annotation.WorkerThread
import org.json.JSONObject
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit

//FIXME: Extract this class to a different package and need to be refactored
data class HttpRequest(var url: String) {
    var requestMethod = "GET"
    var useCaches = true
    var connectionTimeout = TimeUnit.SECONDS.toMillis(10).toInt()
    var readTimeout = TimeUnit.SECONDS.toMillis(10).toInt()
    var allowUserInteraction = false
}

class HttpResponse(val code: Int, val message: String, val source: InputStream? = null) {

    fun sourceAsString(): String? = source?.use {
        return@use BufferedReader(InputStreamReader(it)).readText()
    }

    fun sourceAsJson(): JSONObject = source?.use {
        val response = BufferedReader(InputStreamReader(it)).readText()
        return@use JSONObject(response)
    } ?: JSONObject()
}

object HttpClient {

    init {
        HttpURLConnection.setFollowRedirects(true)
    }

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

            return HttpResponse(urlConnection.responseCode, urlConnection.responseMessage, inputStream)
        } catch (ex: Exception) {
            throw ex
        } finally {
            urlConnection?.disconnect()
        }
    }

}