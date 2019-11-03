package com.jordansilva.imageloader.domain

import com.jordansilva.imageloader.domain.BaseDomain

data class Photo(val id: String, val title: String, val url: String) : BaseDomain()
