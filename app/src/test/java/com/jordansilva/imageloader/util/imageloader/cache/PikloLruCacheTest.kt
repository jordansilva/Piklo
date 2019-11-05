package com.jordansilva.imageloader.util.imageloader.cache

import android.graphics.Bitmap
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
class PikloLruCacheTest {

    lateinit var cacheStorage: PikloImageCache

    private companion object {
        const val IMAGE_KEY = "image1"
        val IMAGE_SAVED: Bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565)
    }

    @Before
    fun setUp() {
        cacheStorage = PikloLruCache()
        cacheStorage.save(IMAGE_KEY, IMAGE_SAVED)
    }

    @Test
    fun `try to get valid key from cache`() {
        val result = cacheStorage.get(IMAGE_KEY)
        assertNotNull(result)
        assertTrue(IMAGE_SAVED.sameAs(result))
    }

    @Test
    fun `try to get invalid key from cache`() {
        val bitmap = cacheStorage.get("invalid key")
        assertNull(bitmap)
    }

    @Test
    fun `save valid bitmap into cache`() {
        val expectedBitmap = Bitmap.createBitmap(123, 123, Bitmap.Config.ARGB_8888)
        val isSaved = cacheStorage.save("image2", expectedBitmap)
        assertTrue(isSaved)

        val result = cacheStorage.get("image2")
        assertTrue(expectedBitmap.sameAs(result))
    }

    @Test
    fun `save invalid bitmap into cache`() {
        val expectedBitmap = Bitmap.createBitmap(123, 123, Bitmap.Config.ARGB_8888)
        expectedBitmap.recycle()

        val isSaved = cacheStorage.save("image2", expectedBitmap)
        assertFalse(isSaved)
    }

    @Test
    fun `trying save a too big bitmap into cache`() {
        var expectedBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        var imageSizeKb = 0

        //Increasing the size of the bitmap until it becomes bigger than cache maxSize.
        while (imageSizeKb < cacheStorage.maxSize()) {
            expectedBitmap = Bitmap.createBitmap(
                expectedBitmap.width + 1000,
                expectedBitmap.height + 1000,
                expectedBitmap.config
            )
            imageSizeKb = expectedBitmap.allocationByteCount / 1024
        }

        val isSaved = cacheStorage.save("image2", expectedBitmap)
        assertFalse(isSaved)
    }

    @Test
    fun `save invalid key into cache`() {
        val expectedBitmap = Bitmap.createBitmap(123, 123, Bitmap.Config.ARGB_8888)
        val isSaved = cacheStorage.save(" ", expectedBitmap)
        assertFalse(isSaved)
    }

    @Test
    fun `remove an image from cache`() {
        assertNotNull(cacheStorage.get(IMAGE_KEY))
        cacheStorage.remove(IMAGE_KEY)
        assertNull(cacheStorage.get(IMAGE_KEY))
    }

    @Test
    fun `clearing cache`() {
        assertTrue(cacheStorage.size() > 0)
        cacheStorage.clear()
        assertEquals(0, cacheStorage.size())
    }

    @Test
    fun `validating the size consumption from cache`() {
        val imageSizeKb = IMAGE_SAVED.allocationByteCount / 1024
        assertEquals(imageSizeKb, cacheStorage.size())
    }
}