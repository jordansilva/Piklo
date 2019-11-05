package com.jordansilva.imageloader.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jordansilva.imageloader.repository.PhotoRepository
import com.jordansilva.imageloader.ui.model.PhotoViewData
import com.jordansilva.imageloader.util.EspressoIdlingResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(private val repository: PhotoRepository) : ViewModel() {

    private val _listOfPhotos = MutableLiveData<List<PhotoViewData>>()
    val listOfPhotos: LiveData<List<PhotoViewData>>
        get() = _listOfPhotos

    private val _viewState = MutableLiveData<FlickrListViewState>()
    val viewState: LiveData<FlickrListViewState>
        get() = _viewState


    private var currentQuery = ""
    private var currentPage = 0

    //FIXME: Refactor pagination
    fun searchPhotos(query: String, page: Int = 1) {
        currentQuery = query.trim()
        currentPage = page

        if (query.isNotEmpty()) {
            EspressoIdlingResource.increment()
            _viewState.value = FlickrListViewState.Loading
            viewModelScope.launch(Dispatchers.Default) {
                repository.listPhotos(query, page)
                    .map { PhotoViewData(it.title, it.url) }
                    .also { photos ->
                        if (currentPage == 1) {
                            _listOfPhotos.postValue(photos)
                        } else {
                            val data = _listOfPhotos.value ?: emptyList()
                            _listOfPhotos.postValue(data + photos)
                        }
                        _viewState.postValue(FlickrListViewState.Completed)
                        EspressoIdlingResource.decrement()
                    }
            }
        } else {
            _listOfPhotos.value = emptyList()
        }
    }

    fun nextPage() = searchPhotos(currentQuery, currentPage + 1)
}

sealed class FlickrListViewState {
    object Loading : FlickrListViewState()
    object Completed : FlickrListViewState()
}