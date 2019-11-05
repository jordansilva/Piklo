package com.jordansilva.imageloader.util.imageloader

import android.graphics.Bitmap
import android.widget.ImageView
import com.jordansilva.imageloader.TestUtils.VALID_IMAGE_URL
import com.jordansilva.imageloader.any
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations.initMocks
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
class PikloRequestBuilderRobolectricTest {

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
    fun `when request a valid url should return a bitmap`() = runBlocking {
        val expectedBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565)
        `when`(piklo.request(any(Piklo.PikloRequest::class.java))).thenReturn(expectedBitmap)

        val requestBuilder = Piklo.PikloRequestBuilder(piklo, VALID_IMAGE_URL)
        val bitmap = requestBuilder.get()
        assertEquals(expectedBitmap, bitmap)
    }

    @Test
    fun `when request a valid url with imageview the bitmap must be set in the imageview`() = runBlocking {
        val expectedBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565)
        `when`(piklo.request(any(Piklo.PikloRequest::class.java))).thenReturn(expectedBitmap)

        val imageView = mock(ImageView::class.java)
        verify(imageView, never()).setImageBitmap(any())

        Piklo.PikloRequestBuilder(piklo, VALID_IMAGE_URL).into(imageView)
        verify(imageView).setImageBitmap(expectedBitmap)
    }

}