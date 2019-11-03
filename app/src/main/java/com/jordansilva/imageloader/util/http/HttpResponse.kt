package com.jordansilva.imageloader.util.http

import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class HttpResponse(val code: Int, val message: String, val source: InputStream? = null) {

    fun sourceAsString(): String? = source?.use {
        return@use BufferedReader(
            InputStreamReader(
                it
            )
        ).readText()
    }

    fun sourceAsJson(): JSONObject = source?.use {
        val response = BufferedReader(
            InputStreamReader(
                it
            )
        ).readText()
        return@use JSONObject(response)
    } ?: JSONObject()
}