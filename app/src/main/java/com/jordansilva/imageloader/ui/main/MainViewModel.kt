package com.jordansilva.imageloader.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jordansilva.imageloader.repository.PhotoRepository
import com.jordansilva.imageloader.ui.model.PhotoViewData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(private val repository: PhotoRepository) : ViewModel() {

    private val _listOfPhotos = MutableLiveData<List<PhotoViewData>>()
    val listOfPhotos: LiveData<List<PhotoViewData>>
        get() = _listOfPhotos

    private var currentQuery = ""
    private var currentPage = 0

    //FIXME: Refactor pagination
    fun searchPhotos(query: String, page: Int = 1) {
        currentQuery = query.trim()
        currentPage = page

        if (currentPage == 1) _listOfPhotos.value = null

        if (query.isNotEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                repository
                    .listPhotos(query, page)
                    .map { PhotoViewData(it.title, it.url) }
                    .also { photos -> _listOfPhotos.postValue(_listOfPhotos.value?.let { it + photos } ?: photos) }
            }
        } else {
            _listOfPhotos.value = emptyList()
        }
    }

    fun nextPage() = searchPhotos(currentQuery, currentPage + 1)
}
