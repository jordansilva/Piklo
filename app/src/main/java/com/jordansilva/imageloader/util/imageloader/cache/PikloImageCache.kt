package com.jordansilva.imageloader.util.imageloader.cache

import android.graphics.Bitmap

interface PikloImageCache {
    fun get(key: String): Bitmap?
    fun save(key: String, image: Bitmap): Boolean
    fun remove(key: String)
    fun clear()
    fun size(): Int
    fun maxSize(): Int
}

