package com.example.measure_app.ui.home.adapter

import androidx.recyclerview.widget.RecyclerView
import com.example.measure_app.databinding.ItemHomeBinding
import com.example.measure_app.room.entity.Home

class HomeViewHolder(val binding: ItemHomeBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(home: Home) {
        binding.tvNameRoom.text = home.name
        binding.tvTimeCreate.text = home.createAt
        binding.imgMore.setOnClickListener {

        }
    }
}
