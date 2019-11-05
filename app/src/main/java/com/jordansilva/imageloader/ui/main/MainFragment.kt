package com.jordansilva.imageloader.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.jordansilva.imageloader.R
import com.jordansilva.imageloader.ui.ViewModelFactoryProducer
import com.jordansilva.imageloader.ui.main.adapter.PhotoAdapter
import com.jordansilva.imageloader.ui.model.PhotoViewData
import com.jordansilva.imageloader.util.InfiniteScrollListener
import com.jordansilva.imageloader.util.extension.clearButtonWithAction
import com.jordansilva.imageloader.util.extension.onEditorAction
import kotlinx.android.synthetic.main.main_fragment.*

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel by viewModels<MainViewModel>(factoryProducer = { ViewModelFactoryProducer(requireContext()) })
    private val photoAdapter = PhotoAdapter()

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
        viewModel.viewState.observe(viewLifecycleOwner, Observer { processState(it) })
    }

    private fun processState(viewState: FlickrListViewState) {
        when (viewState) {
            is FlickrListViewState.Loading -> progressBar.show()
            is FlickrListViewState.Completed -> progressBar.hide()
        }
    }

    private fun initUi() {
        editQuery.clearButtonWithAction { search("") }
        editQuery.onEditorAction(EditorInfo.IME_ACTION_DONE) { search(editQuery.text.toString()) }

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        recyclerView.adapter = photoAdapter
        recyclerView.addOnScrollListener(InfiniteScrollListener(threshold = 10) { nextPage() })
    }

    private fun search(text: String) {
        viewModel.searchPhotos(text)
    }

    private fun nextPage() {
        viewModel.nextPage()
    }

    private fun updateAdapter(data: List<PhotoViewData>?) {
        data?.let {
            photoAdapter.submitList(data)
            emptyView.visibility = if (data.isNullOrEmpty()) View.VISIBLE else View.GONE
        } ?: photoAdapter.submitList(emptyList())
    }
}
