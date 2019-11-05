package com.jordansilva.imageloader.repository.mapper

import com.jordansilva.imageloader.repository.remote.FlickrPhoto
import com.jordansilva.imageloader.repository.remote.FlickrPhotosSearch
import org.json.JSONObject

object FlickrPhotoSearchMapper : Mapper<JSONObject, FlickrPhotosSearch> {
    override fun mapToDomain(source: JSONObject): FlickrPhotosSearch {
        val jsonObject = source.getJSONObject("photos")
        return with(jsonObject) {
            FlickrPhotosSearch(
                page = getInt("page"),
                pages = getInt("pages"),
                perPage = getInt("perpage"),
                total = getInt("total"),
                photos = mapPhotos(jsonObject)
            )
        }
    }

    private fun mapPhotos(source: JSONObject): List<FlickrPhoto> {
        val listOfPhotos = source.getJSONArray("photo")
        val result = mutableListOf<FlickrPhoto>()

        repeat(listOfPhotos.length()) { idx ->
            FlickrPhotoMapper
                .mapToDomain(listOfPhotos.getJSONObject(idx))
                .also { result.add(it) }
        }

        return result
    }

}