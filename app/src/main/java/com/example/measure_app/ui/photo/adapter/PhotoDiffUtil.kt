package com.example.measure_app.ui.photo.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.measure_app.room.entity.Photo

class PhotoDiffUtil : DiffUtil.ItemCallback<Photo>() {
    override fun areItemsTheSame(
        oldItem: Photo,
        newItem: Photo
    ): Boolean {
        return oldItem.idPhoto == newItem.idPhoto
    }

    override fun areContentsTheSame(
        oldItem: Photo,
        newItem: Photo
    ): Boolean {
        return oldItem == newItem
    }
}