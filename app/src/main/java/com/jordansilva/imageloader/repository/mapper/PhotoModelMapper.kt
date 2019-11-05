package com.jordansilva.imageloader.repository.mapper

import com.jordansilva.imageloader.repository.model.Photo
import com.jordansilva.imageloader.repository.remote.FlickrPhoto

object PhotoModelMapper : Mapper<FlickrPhoto, Photo> {
    override fun mapToDomain(source: FlickrPhoto): Photo {
        return Photo(
            id = source.id,
            title = source.title,
            url = buildUrl(source.farm, source.server, source.id, source.secret)
        )
    }

    private fun buildUrl(farm: Int, server: String, id: String, secret: String): String {
        return "https://farm${farm}.static.flickr.com/${server}/${id}_${secret}.jpg"
    }


}