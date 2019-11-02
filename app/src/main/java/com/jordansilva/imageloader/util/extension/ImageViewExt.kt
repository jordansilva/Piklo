package com.jordansilva.imageloader.util.extension

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import android.util.LruCache
import android.widget.ImageView
import com.jordansilva.imageloader.util.HttpClient
import com.jordansilva.imageloader.util.HttpRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


//TODO: Still need to implement a proper ImageLoader
//Threads leaking
//Should cancel the loading
//Limit amount the threads
//Cache

private lateinit var memoryCache: LruCache<String, Bitmap>

private fun initCache() {
    if (!::memoryCache.isInitialized) {
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        val cacheSize = maxMemory / 8
        Log.d("ImageLoader", "maxMemory: $maxMemory, cacheSize: $cacheSize")

        memoryCache = object : LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String, bitmap: Bitmap): Int {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.byteCount / 1024
            }
        }
    }
}

private fun clearCache() {
    if (::memoryCache.isInitialized) {
        memoryCache.evictAll()
    } else {
        initCache()
    }
}

private fun loadFromCache(url: String): Bitmap? {
    return memoryCache[url]
}

private fun saveBitmapInCache(url: String, bitmap: Bitmap): Bitmap {
    val bitmap2 = bitmap.resizeTo(256.px)
    memoryCache.put(url, bitmap2)
    bitmap.recycle()
    return bitmap2
}

fun ImageView.load(url: String) {
    initCache()

    MainScope().launch {
        val bitmap = loadFromCache(url) ?: run {
            imageAlpha = 10
            loadFromNetwork(url)
        }
        setImageBitmap(bitmap)
        imageAlpha = 255
    }
}

private suspend fun loadFromNetwork(url: String): Bitmap? {
    return withContext(Dispatchers.Default) {
        try {
            Log.d("ImageLoader", "Network request")
            val request = HttpRequest(url)
            val response = HttpClient.execute(request)
            val bitmap = BitmapFactory.decodeStream(response.source)
            saveBitmapInCache(url, bitmap)
        } catch (ex: Exception) {
            ex.printStackTrace() //TODO: Fix printStacktrace
            null
        }
    }
}
