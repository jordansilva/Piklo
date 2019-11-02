package com.jordansilva.imageloader.data.remote

import android.util.Log
import com.jordansilva.imageloader.util.HttpClient
import com.jordansilva.imageloader.util.HttpRequest

class FlickrApi {

    private companion object {
        const val API_ADDRESS = "https://api.flickr.com/services/rest/?format=json"
        const val API_KEY = "0acf76ce52efa307e913075865ebb811"
        const val API_SECRET = "931fcce39ba09254"

        const val PAGE_LIMIT = 50

        object ACTION {
            const val PHOTOS_SEARCH = "flickr.photos.search"
        }
    }

    /**
     * API ACTION: flickr.photos.search
     */
    fun searchPhotos(text: String, page: Int = 1): FlickrPhotosSearchResult {
        try {
            Log.d("FlickrAPI", "Search Photos: $text - Page: $page")
            val request = buildRequest(ACTION.PHOTOS_SEARCH)
                .addQuery("text", text)
                .safeSearch()
                .setPage(page)
                .pageLimit(PAGE_LIMIT)

            val response = HttpClient.execute(request)
            val jsonObject = response.sourceAsJson().getJSONObject("photos")

            //FIXME: Extract to Mapper
            val jsonArray = jsonObject.getJSONArray("photo")
            val photos = mutableListOf<FlickrPhoto>()
            repeat(jsonArray.length()) {
                with(jsonArray.getJSONObject(it)) {
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
                    ).also { photo -> photos.add(photo) }
                }
            }

            return with(jsonObject) {
                FlickrPhotosSearchResult(
                    page = getInt("page"),
                    pages = getInt("pages"),
                    perPage = getInt("perpage"),
                    total = getInt("total"),
                    photos = photos
                )
            }
        } catch (ex: Exception) {
            throw ex
        }
    }

    private fun buildRequest(action: String): HttpRequest =
        HttpRequest(API_ADDRESS)
            .action(action)
            .noJsonCallback()
            .addAuthentication()

    private fun HttpRequest.action(action: String): HttpRequest = this.apply { url += "&method=$action" }
    private fun HttpRequest.addAuthentication(): HttpRequest = this.apply { url += "&api_key=$API_KEY" }
    private fun HttpRequest.addQuery(query: String, value: String): HttpRequest = this.apply { url += "&$query=$value" }
    private fun HttpRequest.safeSearch(): HttpRequest = this.apply { url += "&safe_search=1" }
    private fun HttpRequest.noJsonCallback(): HttpRequest = this.apply { url += "&nojsoncallback=1" }
    private fun HttpRequest.pageLimit(limit: Int): HttpRequest = this.apply { url += "&per_page=$limit" }
    private fun HttpRequest.setPage(page: Int): HttpRequest = this.apply { url += "&page=$page" }

}