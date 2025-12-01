package com.example.measure_app.ui.template.adapter

import androidx.recyclerview.widget.RecyclerView
import com.example.measure_app.databinding.ItemTemplateMaterialBinding
import com.example.measure_app.ui.template.data.InfoTemplate

class TemplateViewHolder(val binding: ItemTemplateMaterialBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(data: InfoTemplate){
        binding.tvName.text=data.name
        binding.tvAtt.text=data.info
    }
}