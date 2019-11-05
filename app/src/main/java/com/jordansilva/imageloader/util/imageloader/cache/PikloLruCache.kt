package com.jordansilva.imageloader.util.imageloader.cache

import android.graphics.Bitmap
import android.util.LruCache

class PikloLruCache : PikloImageCache {

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

    override fun get(key: String): Bitmap? = if (key.isNotBlank()) memoryCache[key] else null

    override fun save(key: String, image: Bitmap): Boolean {
        if (key.isBlank() || image.isRecycled)
            return false

        val imageSizeKb = image.allocationByteCount / 1024

        return if (imageSizeKb > maxSize()) {
            memoryCache.remove(key)
            false
        } else {
            memoryCache.put(key, image)
            true
        }
    }

    override fun remove(key: String) {
        if (key.isNotBlank())
            memoryCache.remove(key)
    }

    override fun clear() = memoryCache.evictAll()

    override fun size(): Int = memoryCache.size()

    override fun maxSize(): Int = memoryCache.maxSize()

}