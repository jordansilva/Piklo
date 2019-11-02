package com.jordansilva.imageloader.domain.interactor.photo

import com.jordansilva.imageloader.data.PhotoRepository
import com.jordansilva.imageloader.domain.interactor.BaseUseCase
import com.jordansilva.imageloader.domain.model.Photo

class GetPhotoFeedUseCase(private val photoRepository: PhotoRepository) : BaseUseCase() {

    fun execute(request: Request): List<Photo> {
        return photoRepository.listPhotos(request.query, request.page)
    }

    data class Request(val query: String, val page: Int = 1)
}