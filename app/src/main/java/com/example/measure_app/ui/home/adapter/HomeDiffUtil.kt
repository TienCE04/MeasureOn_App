package com.example.measure_app.ui.home.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.measure_app.room.entity.Home

class HomeDiffUtil : DiffUtil.ItemCallback<Home>() {
    override fun areItemsTheSame(
        oldItem: Home,
        newItem: Home
    ): Boolean {
        return oldItem.idHome == newItem.idHome
    }

    override fun areContentsTheSame(
        oldItem: Home,
        newItem: Home
    ): Boolean {
        return oldItem == newItem
    }

}