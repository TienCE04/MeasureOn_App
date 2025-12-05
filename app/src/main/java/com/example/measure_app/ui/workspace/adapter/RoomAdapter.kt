package com.example.measure_app.ui.workspace.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.room.Room
import com.example.measure_app.databinding.ItemRoomBinding
import com.example.measure_app.room.entity.RoomInHome

class RoomAdapter(private val listener: OnClickItemRoom) :
    ListAdapter<RoomInHome, RoomViewHolder>(RoomDiffUtil()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RoomViewHolder {
        val view = ItemRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RoomViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: RoomViewHolder,
        position: Int
    ) {
        val room = getItem(position)
        holder.bind(room)
        holder.itemView.setOnClickListener {
            listener.clickItem(room.idRoom,room.name)
        }
        holder.binding.imgDelete.setOnClickListener{
            listener.clickItemDelete(room.idRoom)
        }
        holder.binding.imgMakeCopy.setOnClickListener {
            listener.clickItemCopy()
        }
        holder.binding.imgRename.setOnClickListener {
            listener.clickItemRename(room)
        }
        holder.binding.imgShare.setOnClickListener {
            listener.clickItemShare()
        }
    }
}

interface OnClickItemRoom {
    fun clickItemDelete(idRoom:Int)
    fun clickItemCopy()
    fun clickItemRename(room: RoomInHome)
    fun clickItemShare()
    fun clickItem(idRoom: Int, nameRoom: String)
}