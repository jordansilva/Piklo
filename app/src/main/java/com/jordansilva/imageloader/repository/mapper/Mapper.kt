package com.jordansilva.imageloader.repository.mapper

interface Mapper<in T, out D> {
    fun mapToDomain(source: T): D
}
