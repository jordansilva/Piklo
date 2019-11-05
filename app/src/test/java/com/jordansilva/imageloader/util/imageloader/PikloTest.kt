package com.jordansilva.imageloader.util.imageloader

import android.graphics.Bitmap
import com.jordansilva.imageloader.TestUtils
import com.jordansilva.imageloader.TestUtils.INVALID_IMAGE_URL
import com.jordansilva.imageloader.TestUtils.VALID_IMAGE_URL
import com.jordansilva.imageloader.util.imageloader.cache.PikloImageCache
import com.jordansilva.imageloader.util.imageloader.http.PikloHttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyString
import org.mockito.MockitoAnnotations.initMocks
import org.mockito.Spy
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.lang.Thread.sleep

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
class PikloTest {

    @Mock
    lateinit var cache: PikloImageCache

    @Mock
    lateinit var httpClient: PikloHttpClient

    @Spy
    private var testDispatcher = TestCoroutineDispatcher()

    lateinit var piklo: Piklo

    @Before
    fun setUp() {
        initMocks(this)
        Dispatchers.setMain(testDispatcher)

        piklo = Piklo.Builder()
            .network(httpClient)
            .cache(cache)
            .dispatcher(testDispatcher)
            .skipCache(false)
            .build()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }


    @Test
    fun `valid image url from network should return a bitmap`() = runBlocking {
        val inputStream = TestUtils.loadFile(TestUtils.VALID_INPUTSTREAM)
        `when`(httpClient.execute(anyString())).thenReturn(inputStream)

        val request = Piklo.PikloRequest(VALID_IMAGE_URL, VALID_IMAGE_URL)
        val bitmap = piklo.request(request)
        assertNotNull(bitmap)
    }

    @Test
    fun `valid image url from cache should return a bitmap`() = runBlocking {
        val expectedBitmap = Bitmap.createBitmap(123, 123, Bitmap.Config.RGB_565)
        `when`(cache.get(VALID_IMAGE_URL)).thenReturn(expectedBitmap)
        `when`(httpClient.execute(anyString())).thenReturn(null)

        val request = Piklo.PikloRequest(VALID_IMAGE_URL, VALID_IMAGE_URL)
        val bitmap = piklo.request(request)
        assertTrue(expectedBitmap.sameAs(bitmap))
    }

    @Test
    fun `calling load function with a valid url should return a bitmap`() = runBlocking {
        val expectedBitmap = Bitmap.createBitmap(123, 123, Bitmap.Config.RGB_565)
        `when`(cache.get(VALID_IMAGE_URL)).thenReturn(expectedBitmap)
        `when`(httpClient.execute(anyString())).thenReturn(null)

        val request = Piklo.get().load(VALID_IMAGE_URL).buildRequest()
        val bitmap = piklo.request(request)
        assertTrue(expectedBitmap.sameAs(bitmap))
    }

    @Test
    fun `calling request twice with the same id should cancel the first one`() = runBlocking {
        `when`(httpClient.execute(anyString())).thenReturn(TestUtils.loadFile(TestUtils.VALID_INPUTSTREAM))

        val request = Piklo.PikloRequest(VALID_IMAGE_URL, VALID_IMAGE_URL)
        launch { piklo.request(request) }.also { job -> job.invokeOnCompletion { assertTrue(job.isCancelled) } }
        launch { piklo.request(request) }.also { job -> job.invokeOnCompletion { assertTrue(job.isCompleted) } }
        Unit
    }

    @Test
    fun `cancel all`() {
        piklo.cancelAll()
    }

    @Test
    fun `invalid image url should return null`() = runBlocking {
        `when`(httpClient.execute(anyString())).thenReturn(null)

        val request = Piklo.PikloRequest(INVALID_IMAGE_URL, INVALID_IMAGE_URL)
        val bitmap = piklo.request(request)
        assertNull(bitmap)
    }
}