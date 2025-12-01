package com.example.measure_app.ui.home

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.measure_app.R
import com.example.measure_app.databinding.FragmentHomeBinding
import com.example.measure_app.room.dao.HomeDao
import com.example.measure_app.room.database.AppDatabase
import com.example.measure_app.room.entity.Home
import com.example.measure_app.room.repository.HomeRepository
import com.example.measure_app.ui.home.adapter.HomeAdapter
import com.example.measure_app.ui.home.adapter.OnClickPopup
import com.example.measure_app.ui.home.viewmodel.HomeFactory
import com.example.measure_app.ui.home.viewmodel.HomeViewModel
import com.example.measure_app.util.NavOption
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import org.w3c.dom.Text
import java.time.LocalDate

class FragmentHome : Fragment(), View.OnClickListener, OnClickPopup {

    private lateinit var _binding: FragmentHomeBinding
    private val binding get() = _binding

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeRepository: HomeRepository
    private lateinit var homeDao: HomeDao

    private lateinit var homeRcv: RecyclerView
    private lateinit var homeAdapter: HomeAdapter
    private lateinit var bottomSheetDialog: BottomSheetDialog

    private var homeUpdate=Home(name = "", createAt = "")
    private lateinit var dialogRename: Dialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeDao = AppDatabase.getDatabase(requireContext()).homeDao()
        initAdapter()
        initBottomSheet()
        initViewModel()
        initListener()
        initFlow()
    }

    private fun initListener() {
        binding.imgSearch.setOnClickListener(this)
        binding.fragmentHome.setOnClickListener(this)
        binding.imgBluetooth.setOnClickListener(this)
        binding.imgAdd.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.img_search -> {
                binding.searchView.visibility = View.VISIBLE
            }

            R.id.fragment_home -> {
                binding.searchView.visibility = View.GONE
            }
            R.id.img_add->{
                bottomSheetDialog.show()
            }
        }

    }

    private fun initViewModel() {
        homeRepository = HomeRepository(homeDao)
        homeViewModel =
            ViewModelProvider(this, HomeFactory(homeRepository))[HomeViewModel::class.java]
    }

    private fun initFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    homeViewModel.event.collect { msg ->
                        Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show()
                    }
                }
                launch {
                    homeViewModel.homes.collect { list ->
                        homeAdapter.submitList(list)
                    }
                }
            }
        }
    }
    private fun initAdapter(){
        homeRcv=binding.rcvPr
        homeAdapter= HomeAdapter(this)
        homeRcv.adapter=homeAdapter
        homeRcv.layoutManager= GridLayoutManager(requireContext(),2)
    }

    private fun initBottomSheet() {
        bottomSheetDialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottom_sheet, null)
        view.findViewById<ImageView>(R.id.img_create_home).setOnClickListener{
            createHome()
        }
        view.findViewById<ImageView>(R.id.img_create_room).setOnClickListener{
            createRoom()
        }
        bottomSheetDialog.setContentView(view)

        dialogRename= Dialog(requireContext())
        val viewRename=layoutInflater.inflate(R.layout.dialog_rename,null)
        dialogRename.setCanceledOnTouchOutside(false)
        dialogRename.setCancelable(false)
        viewRename.findViewById<TextView>(R.id.tv_cancel).setOnClickListener {
            dialogRename.dismiss()
        }
        viewRename.findViewById<TextView>(R.id.tv_rename_ok).setOnClickListener {
            val name=viewRename.findViewById<EditText>(R.id.edt_rename).text.toString()
            updateNameHome(name)
        }
        dialogRename.setContentView(viewRename)
    }

    private fun createHome(){
        val home = Home(name = "Nhà mới", createAt = "${LocalDate.now()}")
        lifecycleScope.launch {
            homeViewModel.createHome(home)
        }
    }

    override fun clickItem(idHome: Int, nameHome: String) {
        val bundle= Bundle()
        bundle.putInt("idHome",idHome)
        bundle.putString("nameHome",nameHome)
        findNavController().navigate(R.id.action_fragment_home_to_fragment_room,bundle, NavOption.animationFragment)
    }

    private fun createRoom(){

    }

    override fun clickItemCopy() {

    }

    override fun clickItemRename(home: Home) {
        homeUpdate=home
        dialogRename.show()
    }

    override fun clickItemShare() {

    }

    override fun clickItemDelete() {

    }

    private fun updateNameHome(name:String){
        if(name!=""){
            val h= Home(homeUpdate.idHome,name,homeUpdate.createAt,homeUpdate.countRoom)
            lifecycleScope.launch {
                homeViewModel.updateHome(h)
                dialogRename.dismiss()
            }
        }
        else{
            Toast.makeText(requireContext(),"Vui lòng nhập tên mới!", Toast.LENGTH_SHORT).show()
        }
    }

}