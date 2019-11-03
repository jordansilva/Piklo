package com.jordansilva.imageloader.repository

import com.jordansilva.imageloader.repository.model.Photo
import com.jordansilva.imageloader.repository.remote.FlickrApi

class PhotoRepositoryImpl(private val flickrApi: FlickrApi) : PhotoRepository {

    override fun listPhotos(query: String, page: Int): List<Photo> {
        return flickrApi.searchPhotos(query, page)
            .photos
            .map {
                Photo(
                    id = it.id,
                    title = it.title,
                    url = "https://farm${it.farm}.static.flickr.com/${it.server}/${it.id}_${it.secret}.jpg"
                )
            }
    }

}