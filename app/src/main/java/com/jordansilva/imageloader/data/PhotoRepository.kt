package com.jordansilva.imageloader.data

import com.jordansilva.imageloader.domain.Photo

interface PhotoRepository {
    fun listPhotos(query: String, page: Int = 1) : List<Photo>
}