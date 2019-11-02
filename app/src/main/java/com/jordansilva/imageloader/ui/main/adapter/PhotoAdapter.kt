package com.jordansilva.imageloader.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.jordansilva.imageloader.R
import com.jordansilva.imageloader.ui.model.PhotoViewData
import com.jordansilva.imageloader.util.extension.load

class PhotoAdapter(items: List<PhotoViewData>) : RecyclerView.Adapter<PhotoAdapter.ViewHolder>() {

    private var data: MutableList<PhotoViewData> = mutableListOf()

    init {
        addItems(items)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.simple_row_image, parent, false)
        return ViewHolder(view)
    }

    fun addItems(items: List<PhotoViewData>?) = items?.let {
        data.addAll(items)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bindView(item)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view.rootView) {

        private var imageView: ImageView = view.findViewById(R.id.imageItem)

        fun bindView(item: PhotoViewData) {
            imageView.load(item.url)
        }
    }

}