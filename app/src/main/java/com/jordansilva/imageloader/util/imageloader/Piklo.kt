package com.jordansilva.imageloader.util.imageloader

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.ImageView
import com.jordansilva.imageloader.util.imageloader.cache.PikloImageCache
import com.jordansilva.imageloader.util.imageloader.cache.PikloLruCache
import com.jordansilva.imageloader.util.imageloader.http.PikloHttpClient
import com.jordansilva.imageloader.util.imageloader.http.PikloHttpClientImpl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference
import java.util.WeakHashMap

class Piklo private constructor(
        private val cache: PikloImageCache,
        private val network: PikloHttpClient,
        private var useCache: Boolean,
        private val dispatcher: CoroutineDispatcher
) {

    companion object {
        private lateinit var instance: Piklo

        fun get(): Piklo {
            if (!::instance.isInitialized) {
                synchronized(Piklo::class) {
                    instance = Piklo(PikloLruCache(), PikloHttpClientImpl(), true, Dispatchers.Default)
                }
            }
            return instance
        }

        fun Builder() = PikloBuilder()

    }

    private val scope: CoroutineScope = CoroutineScope(dispatcher + SupervisorJob())
    private var activeRequests = WeakHashMap<Any, PikloRequest>()
    private var activeJobs = WeakHashMap<PikloRequest, Job>()

    fun load(url: String): PikloRequestBuilder = load(Uri.parse(url))

    fun load(uri: Uri): PikloRequestBuilder = PikloRequestBuilder(this, uri.toString())

    fun cancel(id: Any) {
        synchronized(id) {
            activeRequests[id]?.let { request ->
                activeJobs[request]?.run {
                    if (!isCompleted)
                        cancel()
                }

                remove(request)
            }
        }
    }

    fun cancelAll() = scope.cancel()

    private fun remove(request: PikloRequest) {
        activeRequests.remove(request.id)
        activeJobs.remove(request)
    }

    suspend fun request(request: PikloRequest): Bitmap? {
        request.target?.let { cancel(it) } ?: cancel(request.id)
        activeRequests[request.target ?: request.id] = request

        val url = request.url
        var bitmap: Bitmap? = null
        if (useCache) {
            bitmap = getFromCache(url)
        }

        return bitmap ?: process(request).await()
    }

    private fun process(request: PikloRequest): Deferred<Bitmap?> {
        return scope.async {
            val url = request.url
            val bitmap = getFromNetwork(url)
            bitmap?.let { cache.save(request.url, it) }

            return@async bitmap
        }.also { deferredJob ->
            activeJobs[request] = deferredJob
            deferredJob.invokeOnCompletion { remove(request) }
        }
    }

    private fun getFromCache(key: String): Bitmap? {
        return cache.get(key)
    }

    private suspend fun getFromNetwork(url: String): Bitmap? {
        return withContext(Dispatchers.Default) {
            try {
                network.execute(url)?.use { inputStream ->
                    return@use BitmapFactory.decodeStream(inputStream)
                }
            } catch (ex: Exception) {
                null
            }
        }
    }

    class PikloBuilder {

        private var cache: PikloImageCache = PikloLruCache()
        private var network: PikloHttpClient = PikloHttpClientImpl()
        private var skipCache: Boolean = false
        private var coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default

        fun cache(memoryCache: PikloImageCache): PikloBuilder {
            cache = memoryCache
            return this
        }

        fun network(networkClient: PikloHttpClient): PikloBuilder {
            network = networkClient
            return this
        }

        fun skipCache(skip: Boolean = true): PikloBuilder {
            skipCache = skip
            return this
        }

        fun dispatcher(dispatcher: CoroutineDispatcher): PikloBuilder {
            coroutineDispatcher = dispatcher
            return this
        }

        fun build(): Piklo {
            return Piklo(cache, network, !skipCache, coroutineDispatcher)
        }
    }

    /**
     * Request Builder
     */
    class PikloRequestBuilder(
            private val piklo: Piklo,
            var url: String,
            dispatcher: CoroutineDispatcher = Dispatchers.Main
    ) {

        private val scope = CoroutineScope(SupervisorJob() + dispatcher)
        private var target: WeakReference<ImageView>? = null

        var id: Any = url

        suspend fun get(): Bitmap? {
            val request = buildRequest()
            return piklo.request(request)
        }

        fun into(imageView: ImageView): Job {
            piklo.cancel(imageView)
            target = WeakReference(imageView)
            return scope.launch {
                target?.get()?.setImageDrawable(null)
                val bitmap = get()
                bitmap?.let { target?.get()?.setImageBitmap(bitmap) }
            }
        }

        fun buildRequest(): PikloRequest {
            return PikloRequest(id, url, target)
        }

    }

    data class PikloRequest(
            val id: Any,
            val url: String,
            private val _target: WeakReference<ImageView>? = null
    ) {
        val target: ImageView?
            get() = _target?.get()
    }
}