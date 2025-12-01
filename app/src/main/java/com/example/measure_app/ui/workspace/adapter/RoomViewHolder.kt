package com.example.measure_app.ui.workspace.adapter

import androidx.recyclerview.widget.RecyclerView
import com.example.measure_app.databinding.ItemRoomBinding
import com.example.measure_app.room.entity.RoomInHome

class RoomViewHolder(val binding: ItemRoomBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(room: RoomInHome) {
        binding.tvNameRoom.text = room.name
        binding.tvTimeCreate.text = room.createAt
    }
}