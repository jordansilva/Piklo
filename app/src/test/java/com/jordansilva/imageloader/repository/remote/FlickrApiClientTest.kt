package com.jordansilva.imageloader.repository.remote

import com.jordansilva.imageloader.FlickrApiMockGenerator
import com.jordansilva.imageloader.any
import com.jordansilva.imageloader.repository.http.HttpClient
import com.jordansilva.imageloader.repository.http.HttpRequest
import com.jordansilva.imageloader.repository.http.HttpResponse
import com.jordansilva.imageloader.repository.mapper.Mapper
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations.initMocks

class FlickrApiClientTest {

    @Mock
    private lateinit var mapper: Mapper<JSONObject, FlickrPhotosSearch>

    @Mock
    private lateinit var httpClient: HttpClient

    private lateinit var flickrApi: FlickrApi

    @Before
    fun setUp() {
        initMocks(this)
        flickrApi = FlickrApiClient(httpClient, mapper)
    }

    @Test
    fun `testing flickr api client returning successful list`() {
        `when`(httpClient.execute(any(HttpRequest::class.java))).thenReturn(HttpResponse(200, "", null))

        val expected = FlickrApiMockGenerator.generateResult(10, 1)
        `when`(mapper.mapToDomain(any(JSONObject::class.java))).thenReturn(expected)

        val result = flickrApi.searchPhotos("kitten")

        assertEquals(1, result.page)
        assertEquals(10, result.perPage)
        assertTrue(result.photos.isNotEmpty())
        assertTrue(result.total >= 1)
        assertTrue(result.pages >= 1)
    }
}