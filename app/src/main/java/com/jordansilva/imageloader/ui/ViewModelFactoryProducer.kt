package com.jordansilva.imageloader.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jordansilva.imageloader.data.PhotoRepository
import com.jordansilva.imageloader.data.PhotoRepositoryImpl
import com.jordansilva.imageloader.data.remote.FlickrApi
import com.jordansilva.imageloader.ui.main.MainViewModel

class ViewModelFactoryProducer : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when (modelClass.simpleName) {
            MainViewModel::class.java.simpleName -> {
                val repository = PhotoRepositoryImpl(FlickrApi())
                modelClass.getConstructor(PhotoRepository::class.java).newInstance(repository)
            }
            else -> super.create(modelClass)
        }
    }
}