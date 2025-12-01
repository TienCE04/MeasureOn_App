package com.example.measure_app.ui.photo.adapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.measure_app.databinding.ItemPictureRoomBinding
import com.example.measure_app.room.entity.Photo

class PhotoViewHolder(val binding: ItemPictureRoomBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(photo: Photo){
        if(photo.thumbnailPath!=null){
            Glide.with(binding.imgRoom.context)
                .load(photo.thumbnailPath)
                .override(130,140)
                .into(binding.imgRoom)
        }
        else{
            Glide.with(binding.imgRoom.context)
                .load(photo.path)
                .override(130,140)
                .into(binding.imgRoom)
        }

    }
}