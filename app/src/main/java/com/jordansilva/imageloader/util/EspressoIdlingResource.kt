package com.jordansilva.imageloader.util

import androidx.test.espresso.idling.CountingIdlingResource
import com.jordansilva.imageloader.BuildConfig


object EspressoIdlingResource {
    val idlingResource = CountingIdlingResource("EspressoIdlingResource")

    fun increment() {
        if (BuildConfig.DEBUG)
            idlingResource.increment()
    }

    fun decrement() {
        if (BuildConfig.DEBUG)
            idlingResource.decrement()
    }
}
