package com.jordansilva.imageloader.data

import com.jordansilva.imageloader.data.remote.FlickrApi
import com.jordansilva.imageloader.domain.model.Photo

class PhotoRepositoryImpl(private val flickrApi: FlickrApi) : PhotoRepository {

    override fun listPhotos(query: String, page: Int): List<Photo> {
        return flickrApi.searchPhotos(query, page)
            .photos
            .map {
                Photo(
                    id = it.id,
                    title = it.title,
                    url = "http://farm${it.farm}.static.flickr.com/${it.server}/${it.id}_${it.secret}.jpg"
                )
            }
    }

}