package com.jordansilva.imageloader.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jordansilva.imageloader.repository.PhotoRepository
import com.jordansilva.imageloader.repository.PhotoRepositoryImpl
import com.jordansilva.imageloader.repository.remote.FlickrApiClient
import com.jordansilva.imageloader.ui.main.MainViewModel
import com.jordansilva.imageloader.repository.http.HttpClientImpl
import com.jordansilva.imageloader.repository.http.network.AndroidNetwork

class ViewModelFactoryProducer(private val context: Context) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when (modelClass.simpleName) {
            MainViewModel::class.java.simpleName -> {
                val httpClient = HttpClientImpl(AndroidNetwork(context))
                val flickrApi = FlickrApiClient(httpClient)
                val repository = PhotoRepositoryImpl(flickrApi)
                modelClass.getConstructor(PhotoRepository::class.java).newInstance(repository)
            }
            else -> super.create(modelClass)
        }
    }
}