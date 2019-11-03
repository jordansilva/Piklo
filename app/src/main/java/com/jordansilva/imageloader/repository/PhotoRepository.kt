package com.jordansilva.imageloader.repository

import com.jordansilva.imageloader.repository.model.Photo

interface PhotoRepository {
    fun listPhotos(query: String, page: Int = 1): List<Photo>
}