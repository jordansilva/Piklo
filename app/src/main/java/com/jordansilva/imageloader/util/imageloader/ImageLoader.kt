package com.jordansilva.imageloader.util.imageloader

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.jordansilva.imageloader.util.extension.px
import com.jordansilva.imageloader.util.extension.resizeTo
import com.jordansilva.imageloader.util.http.HttpClient
import com.jordansilva.imageloader.util.http.HttpRequest
import kotlinx.coroutines.*

object ImageLoader {

    private var imageCache: ImageCache = ImageCache()

    fun load(view: ImageView, url: String) {
        val job = MainScope().launch {
            val bitmap = imageCache.load(url) ?: run {
                view.setImageDrawable(null)
                loadFromNetwork(url)
            }
            view.setImageBitmap(bitmap)
        }

        view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewDetachedFromWindow(v: View?) {
                if (!job.isCompleted)
                    job.cancel()
            }

            override fun onViewAttachedToWindow(v: View?) {

            }
        })



    }

    private suspend fun loadFromNetwork(url: String): Bitmap? {
        return withContext(Dispatchers.Default) {
            try {
                val request = HttpRequest(url)
                val response = HttpClient().execute(request)
                val bitmap = BitmapFactory.decodeStream(response.source)
                val bitmap2 = bitmap.resizeTo(120.px)
                imageCache.save(url, bitmap2)
                bitmap2
            } catch (ex: Exception) {
                Log.d("Exception", "message: ${ex.message}")
                null
            }
        }
    }
}

fun ImageView.load(url: String) {
    ImageLoader.load(this, url)
}


