package com.example.measure_app.ui.template.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.measure_app.databinding.ItemTemplateMaterialBinding
import com.example.measure_app.ui.template.data.InfoTemplate

class TemplateAdapter(private val listener:(InfoTemplate)->Unit): ListAdapter<InfoTemplate, TemplateViewHolder>(TemplateDiffUtil()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TemplateViewHolder {
        val view= ItemTemplateMaterialBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return TemplateViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: TemplateViewHolder,
        position: Int
    ) {
        val data=getItem(position)
        holder.bind(data)
        holder.itemView.setOnClickListener {
            listener(data)
        }
    }
}