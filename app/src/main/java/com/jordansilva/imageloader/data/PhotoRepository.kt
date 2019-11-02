package com.jordansilva.imageloader.data

import com.jordansilva.imageloader.domain.model.Photo

interface PhotoRepository {
    fun listPhotos(query: String, page: Int = 1) : List<Photo>
}