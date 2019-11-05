package com.jordansilva.imageloader

import com.jordansilva.imageloader.repository.remote.FlickrPhoto
import com.jordansilva.imageloader.repository.remote.FlickrPhotosSearch
import java.io.File
import java.io.InputStream

object TestUtils {
    const val VALID_INPUTSTREAM = "inputstream.txt"
    const val VALID_IMAGE_URL = "https://farm1.static.flickr.com/578/23451156376_8983a8ebc7.jpg"
    const val UNKNOWN_HOST_URL = "https://farm0.static.flickr.com/578/23451156376_8983a8ebc7.jpg"
    const val INVALID_IMAGE_URL = ""

    fun loadFile(filename: String): InputStream {
        val classLoader = this.javaClass.classLoader
        val url = classLoader?.getResource(filename)
        return File(url!!.path).inputStream()
    }
}



object FlickrApiMockGenerator {
    fun generateResult(quantityPerPage: Int = 30, page: Int = 1): FlickrPhotosSearch {
        val photos = mutableListOf<FlickrPhoto>()
        repeat(quantityPerPage) { idx ->
            photos.add(
                FlickrPhoto(
                    id = idx.toString(),
                    owner = idx.toString(),
                    secret = idx.toString(),
                    server = idx.toString(),
                    farm = idx,
                    title = idx.toString(),
                    isPublic = idx.toString(),
                    isFriend = idx.toString(),
                    isFamily = idx.toString()
                )
            )
        }

        return FlickrPhotosSearch(
            page = page,
            pages = page + 1,
            perPage = quantityPerPage,
            total = quantityPerPage * page,
            photos = photos
        )
    }
}