package com.jordansilva.imageloader.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jordansilva.imageloader.R
import com.jordansilva.imageloader.ui.model.PhotoViewData
import com.jordansilva.imageloader.util.extension.load

class PhotoAdapter : ListAdapter<PhotoViewData, PhotoAdapter.PhotoViewHolder>(photoViewDataDiff) {

    private companion object {
        val photoViewDataDiff = object : DiffUtil.ItemCallback<PhotoViewData>() {
            override fun areItemsTheSame(oldItem: PhotoViewData, newItem: PhotoViewData): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: PhotoViewData, newItem: PhotoViewData): Boolean {
                return oldItem.url == newItem.url
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.simple_row_image, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val item = getItem(position)
        holder.bindView(item)
    }

    class PhotoViewHolder(view: View) : RecyclerView.ViewHolder(view.rootView) {
        private var imageView: ImageView = view.findViewById(R.id.imageItem)

        fun bindView(item: PhotoViewData) {
            imageView.load(item.url)
        }
    }
}