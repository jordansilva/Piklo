package com.jordansilva.imageloader.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jordansilva.imageloader.domain.interactor.photo.GetPhotoFeedUseCase
import com.jordansilva.imageloader.ui.model.PhotoViewData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class MainViewModel(private val getPhotoFeedUseCase: GetPhotoFeedUseCase) : ViewModel() {

    private val _listOfPhotos = MutableLiveData<List<PhotoViewData>>()
    val listOfPhotos: LiveData<List<PhotoViewData>>
        get() = _listOfPhotos

    //FIXME: Implement pagination
    fun searchPhotos(query: String) {
        //FIXME: Implement a proper Coroutines or Thread Dispatcher
        viewModelScope.async(Dispatchers.IO) {
            getPhotoFeedUseCase
                .execute(GetPhotoFeedUseCase.Request(query))
                .map { PhotoViewData(it.title, it.url) }
                .also { photos -> _listOfPhotos.postValue(photos) }
        }
    }
}
