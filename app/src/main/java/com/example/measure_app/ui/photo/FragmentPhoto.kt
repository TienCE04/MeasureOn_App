package com.example.measure_app.ui.photo

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.measure_app.R
import com.example.measure_app.databinding.FragmentPhotoBinding
import com.example.measure_app.room.dao.ArrowDao
import com.example.measure_app.room.dao.PhotoDao
import com.example.measure_app.room.database.AppDatabase
import com.example.measure_app.room.entity.DrawingDataArrow
import com.example.measure_app.room.entity.Photo
import com.example.measure_app.room.repository.ArrowRepository
import com.example.measure_app.room.repository.PhotoRepository
import com.example.measure_app.ui.material.viewmodel.MaterialFactory
import com.example.measure_app.ui.material.viewmodel.MaterialViewModel
import com.example.measure_app.ui.photo.adapter.OnClickItem
import com.example.measure_app.ui.photo.adapter.PhotoAdapter
import com.example.measure_app.ui.photo.viewmodel.PhotoFactory
import com.example.measure_app.ui.photo.viewmodel.PhotoViewModel
import com.example.measure_app.util.NavOption
import exportHomeRoomArrowExcel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDate

class FragmentPhoto : Fragment(), View.OnClickListener, OnClickItem {

    private lateinit var _binding: FragmentPhotoBinding
    private val binding get() = _binding
    private lateinit var popupMenuAdd: PopupMenu
    private lateinit var popupMenuExport: PopupMenu
    private lateinit var popupClickImage: PopupMenu

    private lateinit var photoRepository: PhotoRepository
    private lateinit var photoViewModel: PhotoViewModel

    private lateinit var arrowViewModel: MaterialViewModel
    private lateinit var arrowRepository: ArrowRepository
    private lateinit var arrowDao: ArrowDao
    private lateinit var photoDao: PhotoDao
    private lateinit var photoAdapter: PhotoAdapter
    private lateinit var photoRcv: RecyclerView
    private lateinit var photoUri: Uri
    private val listPhoto = mutableListOf<Photo>()

    var idRoom = -1
    var nameRoom = ""

    var nameHome = ""

    //permission
    //camera && result
    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                photoViewModel.createPhoto(
                    Photo(
                        idRoom = idRoom,
                        createAt = "${LocalDate.now()}",
                        path = photoUri.toString()
                    )
                )
            }
        }
    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { grated ->
            if (grated) {
                openCamera()
            } else {
                Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    //thư viện ảnh && result
    private val pickGalleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                Log.d("DEBUG_GET_PICTURE","Uri: $uri")
                val picturesDir=File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),"$idRoom")

                if(!picturesDir.exists()){
                    picturesDir.mkdirs()
                }

                //lấy dữ data từ Uri ghi vào file trong app
                val destFile=File(picturesDir,"photo_${System.currentTimeMillis()}.jpg")
                requireContext().contentResolver.openInputStream(uri)?.use { input->
                    destFile.outputStream().use { output->input.copyTo(output) }
                }

                photoViewModel.createPhoto(Photo(idRoom=idRoom, createAt = "${LocalDate.now()}",path = destFile.absolutePath))
                Toast.makeText(requireContext(),"Lấy ảnh thành công!", Toast.LENGTH_SHORT).show()
            }
        }

    private val requestGalleryPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                openGallery()
            } else {
                Toast.makeText(requireContext(), "Gallery permission denied", Toast.LENGTH_SHORT)
                    .show()
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        photoDao = AppDatabase.getDatabase(requireContext()).photoDao()
        arrowDao = AppDatabase.getDatabase(requireContext()).arrowDao()

        idRoom = arguments?.getInt("idRoom").toString().toInt()
        nameRoom = arguments?.getString("nameRoom").toString()
        nameHome = arguments?.getString("nameHome").toString()
        Log.d("DEBUGxx", nameHome)

        initViewModel()
        initFlow()
        initAdapter()
        initPopup()
        initListener()
    }

    private fun initListener() {
        binding.imgAdd.setOnClickListener(this)
        binding.imgBack.setOnClickListener(this)
        binding.imgMore.setOnClickListener(this)
    }

    private fun initViewModel() {
        photoRepository = PhotoRepository(photoDao)
        photoViewModel = ViewModelProvider(
            requireActivity(),
            PhotoFactory(photoRepository)
        )[PhotoViewModel::class.java]
        photoViewModel.setIdRoom(idRoom)

        arrowRepository = ArrowRepository(arrowDao)
        arrowViewModel = ViewModelProvider(
            requireActivity(),
            MaterialFactory(arrowRepository)
        )[MaterialViewModel::class.java]

    }

    private fun initAdapter() {
        photoRcv = binding.rcvListPhoto
        photoAdapter = PhotoAdapter(this)
        photoRcv.adapter = photoAdapter
        photoRcv.layoutManager = GridLayoutManager(requireContext(), 3)
    }

    private fun initFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    photoViewModel.event.collect { msg ->
                        Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show()
                    }
                }
                launch {
                    photoViewModel.photos.collect { list ->
                        arrowViewModel.loadArrowsFromPhotos(list.map { item -> item.idPhoto })
                        listPhoto.clear()
                        listPhoto.addAll(list)
                        photoAdapter.submitList(list)
                    }
                }
            }
        }

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.img_add -> {
                popupMenuAdd.show()
            }

            R.id.img_back -> {
                findNavController().popBackStack()
            }

            R.id.img_more -> {
                popupMenuExport.show()
            }
        }
    }

    private fun initPopup() {
        popupMenuAdd = PopupMenu(requireContext(), binding.imgAdd)
        popupMenuAdd.inflate(R.menu.menu_create_photo)
        popupMenuAdd.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.item_take_photo -> {
                    checkPermissionCameraAndOpen()
                }

                R.id.item_choose -> {
                    checkPermissionGalleryAndOpen()
                }
            }
            true
        }
        popupMenuExport = PopupMenu(requireContext(), binding.imgMore)
        popupMenuExport.inflate(R.menu.menu_export)
        popupMenuExport.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.export_excel -> {
                    lifecycleScope.launch {
                        if(listPhoto.isNotEmpty()){
                            Log.d("DEBUG_LIST_ARROW",arrowViewModel.arrowAllList.value.filterNotNull().toString())
                            exportHomeRoomArrowExcel(
                                requireContext(),
                                nameHome,
                                nameRoom,
                                arrowViewModel.arrowAllList.value.filterNotNull()
                            )
                            Toast.makeText(requireContext(), "Export thành công!", Toast.LENGTH_SHORT).show()
                        }
                        else{
                            Toast.makeText(requireContext(), "Không có ảnh", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            true
        }
    }

    private fun openCamera() {
        //path //idRoom/picture
        val picturesDir =
            File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "$idRoom")

        if (!picturesDir.exists()) {
            picturesDir.mkdirs()
        }

        //sử dụng provider cung cấp đường dẫn dạng content ẩn đường dẫn thật

        val photoFile = File(picturesDir, "photo_${System.currentTimeMillis()}.jpg")

        photoUri = FileProvider.getUriForFile(
            requireContext(), "${requireContext().packageName}.fileprovider", photoFile
        )

        takePictureLauncher.launch(photoUri)

    }

    private fun openGallery() {
        pickGalleryLauncher.launch("image/*")
    }

    private fun checkPermissionCameraAndOpen() {
        val permission = Manifest.permission.CAMERA
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            openCamera()
        } else {
            requestCameraPermission.launch(permission)
        }
    }

    private fun checkPermissionGalleryAndOpen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                openGallery()
            } else {
                requestGalleryPermission.launch(permission)
            }
        } else {
            openGallery()
        }
    }

    override fun onClickItem(photo: Photo,view:View) {
        popupClickImage= PopupMenu(requireContext(),view)
        popupClickImage.inflate(R.menu.menu_click_image)
        popupClickImage.setOnMenuItemClickListener { menuItem->
            when(menuItem.itemId){
                R.id.open->{
                    openPhotoDetail(photo)
                }
                R.id.delete->{
                    photoViewModel.deletePhoto(photo.idPhoto){
                        val pictureDir= File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),"$idRoom")
                        val fileImg=File(pictureDir,photo.path)
                        fileImg.delete()
                    }
                }
                R.id.info->{

                }
                R.id.edit->{

                }
            }
            true
        }
        popupClickImage.show()
    }

    private fun openPhotoDetail(photo: Photo){
        val bundle = Bundle()
        bundle.putInt("idPhoto", photo.idPhoto)
        bundle.putInt("idRoom", idRoom)
        bundle.putString("createAt", photo.createAt)
        bundle.putString("path", photo.path)
        bundle.putString("name", photo.name)
        findNavController().navigate(
            R.id.action_fragment_photo_to_fragment_material, bundle,
            NavOption.animationFragment
        )
    }


}