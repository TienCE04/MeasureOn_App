package com.example.measure_app.ui.template

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.measure_app.R
import com.example.measure_app.databinding.FragmentDialogTemplateBinding
import com.example.measure_app.service.readTemplateListToObject
import com.example.measure_app.ui.template.adapter.TemplateAdapter
import com.example.measure_app.ui.template.data.InfoTemplate
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class FragmentDialogTemplate(private val listener: OnClickDialogTemplate) : DialogFragment(), View.OnClickListener {

    private var _binding: FragmentDialogTemplateBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapterTemplate: TemplateAdapter

    private val listTemplate= mutableListOf<InfoTemplate>()
    private lateinit var rcvTemplate: RecyclerView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding= FragmentDialogTemplateBinding.inflate(layoutInflater)
        val dialog= MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .create()

        isCancelable = false

        listTemplate.add(InfoTemplate("template1","111-222-333"))
        listTemplate.add(InfoTemplate("template2","xxx-yyy-zzz"))
        listTemplate.add(InfoTemplate("template3","aaa-bbb-ccc"))

        getDataFromFileJson()

        initAdapter()
        initListener()
        val metrics = resources.displayMetrics
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        return dialog
    }

    private fun initAdapter(){
        rcvTemplate=binding.rcvTemplate
        adapterTemplate= TemplateAdapter(){infoTemplate->
            listener.clickItemTemplate(infoTemplate)
            dismiss()
        }
        rcvTemplate.adapter=adapterTemplate
        rcvTemplate.layoutManager= LinearLayoutManager(requireContext())
        adapterTemplate.submitList(listTemplate)
    }

    private fun initListener(){
        binding.tvCancel.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.tv_cancel->{
                dismiss()
            }
        }

    }
    private fun getDataFromFileJson(){
        val listInfoTemplate=readTemplateListToObject(requireContext(),"TemplateFile.json")
        listTemplate.addAll(listInfoTemplate)
    }

}



interface OnClickDialogTemplate {
    fun clickItemTemplate(infoTemplate: InfoTemplate)
}