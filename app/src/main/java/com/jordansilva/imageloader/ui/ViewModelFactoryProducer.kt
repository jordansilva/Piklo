package com.jordansilva.imageloader.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jordansilva.imageloader.data.PhotoRepositoryImpl
import com.jordansilva.imageloader.data.remote.FlickrApi
import com.jordansilva.imageloader.domain.interactor.photo.GetPhotoFeedUseCase
import com.jordansilva.imageloader.ui.main.MainViewModel

class ViewModelFactoryProducer : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when (modelClass.simpleName) {
            MainViewModel::class.java.simpleName -> {
                val repository = PhotoRepositoryImpl(FlickrApi())
                val useCase = GetPhotoFeedUseCase(repository)
                modelClass.getConstructor(useCase::class.java).newInstance(useCase)
            }
            else -> super.create(modelClass)
        }
    }
}