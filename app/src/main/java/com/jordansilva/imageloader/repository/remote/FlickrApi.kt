package com.jordansilva.imageloader.repository.remote

import com.jordansilva.imageloader.repository.http.HttpClient
import com.jordansilva.imageloader.repository.http.HttpRequest
import com.jordansilva.imageloader.repository.mapper.FlickrPhotoSearchMapper
import com.jordansilva.imageloader.repository.mapper.Mapper
import org.json.JSONObject

interface FlickrApi {
    fun searchPhotos(text: String, page: Int = 1): FlickrPhotosSearch
}

class FlickrApiClient(private val client: HttpClient,
                      private val mapper: Mapper<JSONObject, FlickrPhotosSearch> = FlickrPhotoSearchMapper) : FlickrApi {

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
    override fun searchPhotos(text: String, page: Int): FlickrPhotosSearch {
        try {
            if (text.isBlank())
                error("Invalid query for search photos! Please provide a valid text!")

            val request = buildRequest(ACTION.PHOTOS_SEARCH)
                .addQuery("text", text)
                .safeSearch()
                .setPage(page)
                .pageLimit(PAGE_LIMIT)

            val response = client.execute(request)
            return mapper.mapToDomain(response.sourceAsJson())
        } catch (ex: Exception) {
            throw ex
        }
    }

    private fun buildRequest(action: String) =
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