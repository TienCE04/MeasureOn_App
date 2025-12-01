package com.example.measure_app.ui.template.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.measure_app.ui.template.data.InfoTemplate

class TemplateDiffUtil: DiffUtil.ItemCallback<InfoTemplate>() {
    override fun areItemsTheSame(
        oldItem: InfoTemplate,
        newItem: InfoTemplate
    ): Boolean {
        return oldItem.name==newItem.name
    }

    override fun areContentsTheSame(
        oldItem: InfoTemplate,
        newItem: InfoTemplate
    ): Boolean {
        return oldItem==newItem
    }
}