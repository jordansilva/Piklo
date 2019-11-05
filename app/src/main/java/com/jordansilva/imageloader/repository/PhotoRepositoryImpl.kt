package com.jordansilva.imageloader.repository

import com.jordansilva.imageloader.repository.mapper.PhotoModelMapper
import com.jordansilva.imageloader.repository.model.Photo
import com.jordansilva.imageloader.repository.remote.FlickrApi

class PhotoRepositoryImpl(private val flickrApi: FlickrApi) : PhotoRepository {

    override fun listPhotos(query: String, page: Int): List<Photo> {
        return try {
            val response = flickrApi.searchPhotos(query, page)
            response.photos.map { PhotoModelMapper.mapToDomain(it) }
        } catch (ex: Exception) {
            emptyList()
        }
    }

}