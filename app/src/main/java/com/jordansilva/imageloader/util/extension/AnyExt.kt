package com.jordansilva.imageloader.util.extension

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Matrix

val Int.dp: Float
    get() = (this / Resources.getSystem().displayMetrics.density)

val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()


fun Bitmap.resizeTo(size: Int): Bitmap {
    var newWidth = size
    var newHeight = size
    when {
        width > height -> newHeight = (size * height) / width
        width < height -> newWidth = (size * width) / height
    }

    val matrix = Matrix().apply {
        postScale(
            newWidth.toFloat() / width,
            newHeight.toFloat() / height
        )
    }
    val newBitmap = Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    recycle()
    return newBitmap
}

