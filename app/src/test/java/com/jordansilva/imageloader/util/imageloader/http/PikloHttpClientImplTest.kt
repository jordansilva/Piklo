package com.jordansilva.imageloader.util.imageloader.http

import com.jordansilva.imageloader.TestUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.UnknownHostException

class PikloHttpClientImplTest {

    lateinit var httpClient: PikloHttpClient

    @Before
    fun setUp() {
        httpClient = PikloHttpClientImpl()
    }

    @Test
    fun `testing valid url request`() {
        val expected = TestUtils.loadFile(TestUtils.VALID_INPUTSTREAM)

        val result = httpClient.execute(TestUtils.VALID_IMAGE_URL)
        assertNotNull(result)
        assertEquals(
            InputStreamReader(expected).readText(),
            InputStreamReader(result).readText()
        )
    }

    @Test(expected = MalformedURLException::class)
    fun `testing invalid url request`() {
        httpClient.execute(TestUtils.INVALID_IMAGE_URL)
    }

    @Test(expected = UnknownHostException::class)
    fun `testing an unknown host url request`() {
        httpClient.execute(TestUtils.UNKNOWN_HOST_URL)
    }
}