package com.jordansilva.imageloader.util

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class InfiniteScrollListener(private val threshold: Int = THRESHOLD, val block: () -> Unit) : RecyclerView.OnScrollListener() {

    private companion object {
        const val THRESHOLD = 10
    }

    private var loading = true
    private var previousTotalCount = 0

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val totalItemCount = recyclerView.layoutManager?.itemCount ?: 0
        val lastItemPosition = (recyclerView.layoutManager as GridLayoutManager).findLastVisibleItemPosition()

        //This condition happens when the RecyclerView is cleaned.
        if (previousTotalCount > totalItemCount || (loading && totalItemCount > previousTotalCount)) {
            loading = false
            previousTotalCount = totalItemCount
        }

        //Close to the
        if (!loading && lastItemPosition + threshold >= totalItemCount) {
            loading = true
            block()
        }
    }
}
