package com.jordansilva.imageloader.util.imageloader

import android.widget.ImageView
import com.jordansilva.imageloader.TestUtils.INVALID_IMAGE_URL
import com.jordansilva.imageloader.TestUtils.VALID_IMAGE_URL
import com.jordansilva.imageloader.any
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations.initMocks

class PikloRequestBuilderTest {

    @Mock
    lateinit var piklo: Piklo

    private var testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setUp() {
        initMocks(this)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `validate request builder Uri`() {
        val requestBuilder = Piklo.PikloRequestBuilder(piklo, VALID_IMAGE_URL)
        assertEquals(VALID_IMAGE_URL, requestBuilder.id)
        assertEquals(VALID_IMAGE_URL, requestBuilder.url)
    }

    @Test
    fun `when request an invalid url should return null`() = runBlocking {
        `when`(piklo.request(any(Piklo.PikloRequest::class.java))).thenReturn(null)

        val requestBuilder = Piklo.PikloRequestBuilder(piklo, INVALID_IMAGE_URL)
        val bitmap = requestBuilder.get()
        assertNull(bitmap)
    }

    @Test
    fun `when request an invalid url with imageview the bitmap should not be set in the imageview`() = runBlocking {
        `when`(piklo.request(any(Piklo.PikloRequest::class.java))).thenReturn(null)

        val imageView = mock(ImageView::class.java)
        verify(imageView, never()).setImageBitmap(any())

        Piklo.PikloRequestBuilder(piklo, VALID_IMAGE_URL).into(imageView)
        verify(imageView, never()).setImageBitmap(any())
    }

}