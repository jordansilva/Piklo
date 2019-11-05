package com.jordansilva.imageloader.repository.mapper

import com.jordansilva.imageloader.repository.remote.FlickrPhoto
import org.json.JSONObject

object FlickrPhotoMapper : Mapper<JSONObject, FlickrPhoto> {
    override fun mapToDomain(source: JSONObject): FlickrPhoto {
        return with(source) {
            FlickrPhoto(
                id = getString("id"),
                owner = getString("owner"),
                secret = getString("secret"),
                server = getString("server"),
                farm = getInt("farm"),
                title = getString("title"),
                isPublic = getString("ispublic"),
                isFriend = getString("isfriend"),
                isFamily = getString("isfamily")
            )
        }
    }
}