package com.example.measure_app.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.ListAdapter
import com.example.measure_app.R
import com.example.measure_app.databinding.ItemHomeBinding
import com.example.measure_app.room.entity.Home

class HomeAdapter(private val listener: OnClickPopup) :
    ListAdapter<Home, HomeViewHolder>(HomeDiffUtil()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HomeViewHolder {
        val view = ItemHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: HomeViewHolder,
        position: Int
    ) {
        val home = getItem(position)
        holder.bind(home)
        holder.itemView.setOnClickListener {
            listener.clickItem(home.idHome, home.name)
        }
        holder.binding.imgMore.setOnClickListener {
            val popup = PopupMenu(holder.itemView.context, it)
            popup.inflate(R.menu.menu_item_home)

            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.item_copy -> {
                        listener.clickItemCopy()
                    }

                    R.id.item_rename -> {
                        listener.clickItemRename(home)
                    }

                    R.id.item_share -> {
                        listener.clickItemShare()
                    }

                    R.id.item_delete -> {
                        listener.clickItemDelete()
                    }
                }
                true
            }
            popup.show()
        }
    }
}

interface OnClickPopup {
    fun clickItemCopy()
    fun clickItemRename(home: Home)
    fun clickItemShare()
    fun clickItemDelete()

    fun clickItem(idHome: Int, nameHome: String)
}
