package com.jordansilva.imageloader.util.imageloader.extension

import android.widget.ImageView
import com.jordansilva.imageloader.util.imageloader.Piklo

fun ImageView.load(url: String) {
    Piklo.get()
        .load(url)
        .into(this)
}