package com.jordansilva.imageloader

import org.mockito.Mockito

fun <T> any(type: Class<T>): T = Mockito.any<T>(type)