package com.jordansilva.imageloader.data

import android.util.Log
import com.jordansilva.imageloader.data.remote.FlickrApi
import com.jordansilva.imageloader.domain.Photo

class PhotoRepositoryImpl(private val flickrApi: FlickrApi) : PhotoRepository {

    override fun listPhotos(query: String, page: Int): List<Photo> {
        Log.d("PhotoRepository", "query: $query - page: $page")
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