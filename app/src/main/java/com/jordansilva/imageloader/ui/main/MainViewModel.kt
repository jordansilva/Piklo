package com.jordansilva.imageloader.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jordansilva.imageloader.data.PhotoRepository
import com.jordansilva.imageloader.ui.model.PhotoViewData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(private val repository: PhotoRepository) : ViewModel() {

    private val _listOfPhotos = MutableLiveData<List<PhotoViewData>>()
    val listOfPhotos: LiveData<List<PhotoViewData>>
        get() = _listOfPhotos

    //FIXME: Implement pagination
    fun searchPhotos(query: String, page: Int = 1) {
        viewModelScope.launch(Dispatchers.IO) {
            repository
                .listPhotos(query, page)
                .map { PhotoViewData(it.title, it.url) }
                .also { photos -> _listOfPhotos.postValue(_listOfPhotos.value?.let { it + photos } ?: photos) }
        }
    }
}
