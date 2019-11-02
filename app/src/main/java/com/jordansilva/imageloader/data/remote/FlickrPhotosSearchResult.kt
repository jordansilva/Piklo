package com.jordansilva.imageloader.data.remote

data class FlickrPhotosSearchResult(
        val page: Int,
        val pages: Int,
        val perPage: Int,
        val total: Int,
        val photos: List<FlickrPhoto>
)

data class FlickrPhoto(
        val id: String,
        val owner: String,
        val secret: String,
        val server: String,
        val farm: Int,
        val title: String,
        val isPublic: String,
        val isFriend: String,
        val isFamily: String
)