package com.example.measure_app.ui.workspace

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.measure_app.R
import com.example.measure_app.databinding.FragmentRoomBinding
import com.example.measure_app.room.dao.RoomDao
import com.example.measure_app.room.database.AppDatabase
import com.example.measure_app.room.entity.RoomInHome
import com.example.measure_app.room.repository.RoomInHomeRepository
import com.example.measure_app.ui.workspace.adapter.OnClickItemRoom
import com.example.measure_app.ui.workspace.adapter.RoomAdapter
import com.example.measure_app.ui.workspace.viewmodel.RoomFactory
import com.example.measure_app.ui.workspace.viewmodel.RoomViewModel
import com.example.measure_app.util.NavOption
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate

class FragmentRoom : Fragment(), View.OnClickListener, OnClickItemRoom {
    private lateinit var _binding: FragmentRoomBinding
    private val binding get() = _binding

    private lateinit var roomViewModel: RoomViewModel
    private lateinit var roomInHomeRepository: RoomInHomeRepository
    private lateinit var roomDao: RoomDao
    private lateinit var roomRcv: RecyclerView
    private lateinit var roomAdapter: RoomAdapter

    private lateinit var dialogAdd: Dialog

    private var nameHome = ""
    private var idHome = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //
        roomDao = AppDatabase.getDatabase(requireContext()).roomDao()

        initDialog()
        initAdapter()
        initViewModel()
        getDataFromBundle()
        initListener()
        initFlow()

    }

    private fun initListener() {
        binding.imgMore.setOnClickListener(this)
        binding.imgBack.setOnClickListener(this)
        binding.imgAdd.setOnClickListener(this)
        binding.imgBluetooth.setOnClickListener(this)

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.img_more -> {

            }

            R.id.img_back -> {
                findNavController().popBackStack()
            }
            R.id.img_bluetooth->{

            }
            R.id.img_add->{
                dialogAdd.show()
            }
        }
    }

    private fun getDataFromBundle() {
        idHome = arguments?.getInt("idHome").toString().toInt()
        nameHome = arguments?.getString("nameHome").toString()
        binding.tvRoom.text = nameHome
        roomViewModel.setIdHome(idHome)
    }

    private fun initAdapter() {
        roomRcv = binding.rcvRoomDetail
        roomAdapter = RoomAdapter(this)
        roomRcv.adapter = roomAdapter
        roomRcv.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun initViewModel() {
        roomInHomeRepository= RoomInHomeRepository(roomDao)
        roomViewModel =
            ViewModelProvider(this, RoomFactory(roomInHomeRepository))[RoomViewModel::class.java]
    }

    //gắn với vòng đời view fragment tốt hơn ví dụ Started ->collect bắt đầu
    private fun initFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    roomViewModel.event.collect { msg ->
                        Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show()
                    }
                }
                launch {
                    roomViewModel.rooms.collectLatest { list ->
                        roomAdapter.submitList(list)
                    }
                }
            }
        }
    }

    override fun clickItemDelete() {
        Toast.makeText(requireContext(),"Click Delete", Toast.LENGTH_SHORT).show()
    }

    override fun clickItemCopy() {
        Toast.makeText(requireContext(),"Click Copy", Toast.LENGTH_SHORT).show()
    }

    override fun clickItemRename() {
        Toast.makeText(requireContext(),"Click Rename", Toast.LENGTH_SHORT).show()
    }

    override fun clickItemShare() {
        Toast.makeText(requireContext(),"Click Share", Toast.LENGTH_SHORT).show()
    }

    override fun clickItem(idRoom: Int, nameRoom: String) {
        val bundle= Bundle()
        bundle.putInt("idRoom",idRoom)
        bundle.putString("nameRoom",nameRoom)
        findNavController().navigate(R.id.action_fragment_room_to_fragment_photo,bundle, NavOption.animationFragment)
    }

    private fun initDialog(){
        dialogAdd= Dialog(requireContext())
        val view=layoutInflater.inflate(R.layout.dialog_create_room,null)
        dialogAdd.setCancelable(false)
        dialogAdd.setCanceledOnTouchOutside(false)
        view.findViewById<TextView>(R.id.tv_rename_ok).setOnClickListener {
            val name=view.findViewById<EditText>(R.id.edt_rename).text.toString()
            createRoom(name)
        }
        view.findViewById<TextView>(R.id.tv_cancel).setOnClickListener {
            dialogAdd.dismiss()
        }
        dialogAdd.setContentView(view)
    }

    private fun createRoom(name:String){
        if(name!=""){
            roomViewModel.createRoom(RoomInHome(name = name, idHome=idHome,createAt = "${LocalDate.now()}"))
            dialogAdd.dismiss()
        }
        else{
            Toast.makeText(requireContext(),"Vui lòng nhập tên phòng!", Toast.LENGTH_SHORT).show()
        }
    }
}