package com.jordansilva.imageloader.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jordansilva.imageloader.R
import com.jordansilva.imageloader.ui.ViewModelFactoryProducer
import com.jordansilva.imageloader.ui.main.adapter.PhotoAdapter
import com.jordansilva.imageloader.ui.model.PhotoViewData
import kotlinx.android.synthetic.main.main_fragment.recyclerView

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel by viewModels<MainViewModel>(factoryProducer = { ViewModelFactoryProducer() })
    private val adapter = PhotoAdapter()

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
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            val threshold = 10
            var loading = true
            var previousTotalCount = 0

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val totalItemCount = recyclerView.layoutManager?.itemCount ?: 0
                val lastItemPosition = (recyclerView.layoutManager as GridLayoutManager).findLastVisibleItemPosition()

                if (loading && totalItemCount > previousTotalCount) {
                    loading = false
                    previousTotalCount = totalItemCount
                }

                if (!loading && lastItemPosition + threshold >= totalItemCount) {
                    loading = true
                    viewModel.searchPhotos("kittens", (totalItemCount / 50) + 1)
                }
            }
        })

        if (viewModel.listOfPhotos.value.isNullOrEmpty()) {
            viewModel.searchPhotos("kittens")
        }
    }

    private fun updateAdapter(data: List<PhotoViewData>?) {
        data?.let {
            adapter.submitList(data)
        }
    }
}
