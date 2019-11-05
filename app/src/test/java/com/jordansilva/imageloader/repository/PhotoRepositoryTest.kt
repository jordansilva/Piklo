package com.jordansilva.imageloader.repository

import com.jordansilva.imageloader.FlickrApiMockGenerator
import com.jordansilva.imageloader.repository.remote.FlickrApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyInt
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock

class PhotoRepositoryTest {

    private lateinit var repository: PhotoRepository
    private lateinit var flickrApi: FlickrApi

    @Before
    fun setUp() {
        flickrApi = mock(FlickrApi::class.java)
        repository = PhotoRepositoryImpl(flickrApi)
    }

    @Test
    fun `when list photos with valid query - should return a list of photos`() {
        val expectedQuantity = 30
        val expectedResult = FlickrApiMockGenerator.generateResult(expectedQuantity)
        `when`(flickrApi.searchPhotos(anyString(), anyInt())).thenReturn(expectedResult)

        val listOfPhotos = repository.listPhotos("kittens")
        assertEquals(expectedQuantity, listOfPhotos.size)
    }

    @Test
    fun `when list photos with invalid query - should return a empty list`() {
        val expectedQuantity = 0
        val expectedResult = FlickrApiMockGenerator.generateResult(expectedQuantity)
        `when`(flickrApi.searchPhotos(anyString(), anyInt())).thenReturn(expectedResult)

        val listOfPhotos = repository.listPhotos("nonexistentquery")
        assertTrue(listOfPhotos.isEmpty())
    }

    @Test(expected = Exception::class)
    fun `when list photos with empty query - should return a empty list`() {
        `when`(flickrApi.searchPhotos("", anyInt())).thenThrow(IllegalStateException("Invalid"))
        repository.listPhotos("")
    }
}


