package com.jordansilva.imageloader.util.imageloader

import android.graphics.Bitmap
import android.util.LruCache
import com.jordansilva.imageloader.util.extension.px
import com.jordansilva.imageloader.util.extension.resizeTo

class ImageCache {

    private val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
    private lateinit var memoryCache: LruCache<String, Bitmap>

    init {
        initMemoryCache()
    }

    private fun initMemoryCache() {
        val cacheSize = maxMemory / 8

        memoryCache = object : LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String, bitmap: Bitmap): Int {
                return bitmap.byteCount / 1024
            }
        }
    }

    fun save(key: String, image: Bitmap) {
        memoryCache.put(key, image)
    }

    fun load(url: String): Bitmap? {
        return memoryCache[url]
    }

    fun clearCache() {
        memoryCache.evictAll()
    }

}