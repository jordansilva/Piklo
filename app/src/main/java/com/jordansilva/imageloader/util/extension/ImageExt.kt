package com.jordansilva.imageloader.util.extension

import android.widget.ImageView
import com.jordansilva.imageloader.util.imageloader.ImageLoader

fun ImageView.load(url: String) {
    ImageLoader.load(this, url)
}