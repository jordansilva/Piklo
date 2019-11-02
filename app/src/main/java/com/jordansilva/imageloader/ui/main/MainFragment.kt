package com.jordansilva.imageloader.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.GridLayoutManager
import com.jordansilva.imageloader.R
import com.jordansilva.imageloader.data.PhotoRepositoryImpl
import com.jordansilva.imageloader.data.remote.FlickrApi
import com.jordansilva.imageloader.domain.interactor.photo.GetPhotoFeedUseCase
import com.jordansilva.imageloader.ui.ViewModelFactoryProducer
import com.jordansilva.imageloader.ui.main.adapter.PhotoAdapter
import com.jordansilva.imageloader.ui.model.PhotoViewData
import kotlinx.android.synthetic.main.main_fragment.recyclerView

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel by viewModels<MainViewModel>(factoryProducer = { ViewModelFactoryProducer() })
    private val adapter = PhotoAdapter(emptyList())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initObservers()
        initUi()
    }

    private fun initObservers() {
        viewModel.listOfPhotos.observe(viewLifecycleOwner, Observer { updateAdapter(it) })
    }

    private fun initUi() {
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        recyclerView.adapter = adapter

        if (viewModel.listOfPhotos.value.isNullOrEmpty()) {
            viewModel.searchPhotos("kittens")
        }
    }

    private fun updateAdapter(data: List<PhotoViewData>?) {
        data?.let {
            adapter.addItems(data)
        }
    }

    private fun populateDogs() {
        val list = mutableListOf<PhotoViewData>()
        repeat(100) {
            list.add(PhotoViewData("Dog", "http://farm1.static.flickr.com/578/23451156376_8983a8ebc7.jpg"))
        }
        updateAdapter(list)
    }


}
