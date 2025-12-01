package com.example.measure_app.ui.photo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.measure_app.databinding.ItemPictureRoomBinding
import com.example.measure_app.room.entity.Photo

class PhotoAdapter(private val listener: OnClickItem) : ListAdapter<Photo, PhotoViewHolder>(PhotoDiffUtil()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PhotoViewHolder {
        val view =
            ItemPictureRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: PhotoViewHolder,
        position: Int
    ) {
        val photo = getItem(position)
        holder.bind(photo)
        holder.itemView.setOnClickListener {
            listener.onClickItem(photo)
        }
    }
}

interface OnClickItem{
    fun onClickItem(photo: Photo)
}