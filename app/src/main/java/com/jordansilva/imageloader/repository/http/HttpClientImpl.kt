package com.jordansilva.imageloader.repository.http

import androidx.annotation.WorkerThread
import com.jordansilva.imageloader.repository.http.network.NetworkConnection
import com.jordansilva.imageloader.util.EspressoIdlingResource
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class HttpClientImpl(private val networkConnection: NetworkConnection) : HttpClient {

    @WorkerThread
    private fun openConnection(request: HttpRequest): HttpURLConnection {
        try {
            val urlConnection = URL(request.url).openConnection() as HttpURLConnection
            urlConnection.run {
                requestMethod = request.requestMethod
                useCaches = request.useCaches
                connectTimeout = request.connectionTimeout
                readTimeout = request.readTimeout
                connect()
            }
            return urlConnection
        } catch (ex: Exception) {
            throw ex
        }
    }

    @WorkerThread
    override fun execute(request: HttpRequest): HttpResponse {
        if (!networkConnection.isConnected())
            error("Internet connection unavailable")

        var connection: HttpURLConnection? = null
        return try {
            EspressoIdlingResource.increment()
            connection = openConnection(request)

            var inputStream: InputStream? = null

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val byteOutputStream = ByteArrayOutputStream()
                connection.inputStream.copyTo(byteOutputStream)
                inputStream = ByteArrayInputStream(byteOutputStream.toByteArray())
            }

            HttpResponse(
                code = connection.responseCode,
                message = connection.responseMessage,
                source = inputStream
            )
        } catch (ex: Exception) {
            throw ex
        } finally {
            connection?.inputStream?.close()
            connection?.disconnect()
            EspressoIdlingResource.decrement()
        }
    }

}