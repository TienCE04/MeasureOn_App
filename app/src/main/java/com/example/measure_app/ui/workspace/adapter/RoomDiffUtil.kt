package com.example.measure_app.ui.workspace.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.measure_app.room.entity.RoomInHome

class RoomDiffUtil : DiffUtil.ItemCallback<RoomInHome>() {
    override fun areItemsTheSame(
        oldItem: RoomInHome,
        newItem: RoomInHome
    ): Boolean {
        return oldItem.idRoom == newItem.idRoom
    }

    override fun areContentsTheSame(
        oldItem: RoomInHome,
        newItem: RoomInHome
    ): Boolean {
        return oldItem == newItem
    }
}