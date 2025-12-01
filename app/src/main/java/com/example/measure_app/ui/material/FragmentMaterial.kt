package com.example.measure_app.ui.material

import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.measure_app.R
import com.example.measure_app.canvas.arrow.ArrowDataView
import com.example.measure_app.canvas.arrow.DrawView
import com.example.measure_app.canvas.arrow.OnClickDraw
import com.example.measure_app.databinding.DialogInfoMaterialBinding
import com.example.measure_app.databinding.FragmentRoomDetailBinding
import com.example.measure_app.room.dao.ArrowDao
import com.example.measure_app.room.dao.PhotoDao
import com.example.measure_app.room.database.AppDatabase
import com.example.measure_app.room.repository.ArrowRepository
import com.example.measure_app.room.repository.PhotoRepository
import com.example.measure_app.ui.material.viewmodel.MaterialFactory
import com.example.measure_app.ui.material.viewmodel.MaterialViewModel
import com.example.measure_app.ui.photo.viewmodel.PhotoFactory
import com.example.measure_app.ui.photo.viewmodel.PhotoViewModel
import com.example.measure_app.ui.template.FragmentDialogTemplate
import com.example.measure_app.ui.template.OnClickDialogTemplate
import com.example.measure_app.ui.template.data.InfoTemplate
import com.example.measure_app.util.convert.toArrowDataView
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import androidx.core.graphics.createBitmap

class FragmentMaterial : Fragment(), View.OnClickListener, OnClickDraw, OnClickDialogTemplate {

    private lateinit var _binding: FragmentRoomDetailBinding
    private val binding get() = _binding
    private lateinit var arrowViewModel: MaterialViewModel
    private lateinit var arrowRepository: ArrowRepository
    private lateinit var arrowDao: ArrowDao
    private lateinit var photoRepository: PhotoRepository
    private lateinit var photoViewModel: PhotoViewModel
    private lateinit var photoDao: PhotoDao
    private var isSelectDraw = false

    private lateinit var bottomSheetDialog: BottomSheetDialog

    private lateinit var dialogInfo: Dialog
    private lateinit var bindingDialogInfo: DialogInfoMaterialBinding
    private var arrowReceive = ArrowDataView(0f, 0f, 0f, 0f, -1, 0.0f)
    private lateinit var photoView: PhotoView
    var idRoom = -1
    var idPhoto = -1
    var namePhoto = ""
    var pathPhoto = ""
    var createAt = ""
    var nameMaterial = ""
    var attr1 = ""
    var attr2 = ""
    var attr3 = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoomDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //
        arrowDao = AppDatabase.getDatabase(requireContext()).arrowDao()

        photoDao= AppDatabase.getDatabase(requireContext()).photoDao()

        getDataFromBundle()
        initFlow()
        initViewModel()
        bindingDialogInfo = DialogInfoMaterialBinding.inflate(layoutInflater)
        photoView = binding.photoRoomDetail
        binding.drawView.setPhotoView(photoView)
        initDialogInfo()
        initBottomSheet()
        binding.tvNameRoomDetail.text = namePhoto
        Glide.with(requireContext())
            .load(pathPhoto)
            .into(binding.photoRoomDetail)
        initListener()
    }

    private fun initListener() {
        binding.tvDone.setOnClickListener(this)
        binding.imgShare.setOnClickListener(this)
        binding.imgMore.setOnClickListener(this)
        binding.iconAngle.setOnClickListener(this)
        binding.iconArea.setOnClickListener(this)
        binding.iconArrow.setOnClickListener(this)
        binding.iconDraw.setOnClickListener(this)
        binding.iconText.setOnClickListener(this)

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tv_done -> {
                lifecycleScope.launch {
                    val arrowDataView=binding.drawView.convertToDB(idPhoto)
                    arrowViewModel.saveArrows(arrowDataView)
                }
                lifecycleScope.launch {
                    savePhotoWithDrawing()
                    findNavController().popBackStack()
                }

            }

            R.id.icon_arrow -> {
                if (isSelectDraw) {
                    isSelectDraw = false
                    binding.drawView.setMode(DrawView.Mode.NULL)
                    binding.iconArrow.imageTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.black
                        )
                    )
                } else {
                    isSelectDraw = true
                    binding.iconArrow.imageTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.icon_draw
                        )
                    )
                    binding.drawView.onClickDrawListener = this
                    binding.drawView.setMode(DrawView.Mode.ARROW)
                }
            }

            R.id.cv_cancel -> {
                dialogInfo.dismiss()
            }

            R.id.cv_save -> {
                arrowReceive.nameMaterial = bindingDialogInfo.edtName.text.toString()
                arrowReceive.attribute1 = bindingDialogInfo.edtInfo1.text.toString()
                arrowReceive.attribute2 = bindingDialogInfo.edtInfo2.text.toString()
                arrowReceive.attribute3 = bindingDialogInfo.edtInfo3.text.toString()
                binding.drawView.drawAgain()
                dialogInfo.dismiss()
            }

            R.id.cv_delete -> {

            }

            R.id.img_add -> {
                showDialogTemplate()
            }
        }
    }

    private fun getDataFromBundle() {
        idRoom = arguments?.getInt("idRoom").toString().toInt()
        idPhoto = arguments?.getInt("idPhoto").toString().toInt()
        namePhoto = arguments?.getString("name").toString()
        createAt = arguments?.getString("createAt").toString()
        pathPhoto = arguments?.getString("path").toString()
    }

    override fun onArrowSelected(arrow: ArrowDataView) {
        arrowReceive = arrow
        bottomSheetDialog.show()
    }

    override fun onSelectionCleared() {
        bottomSheetDialog.dismiss()
    }

    private fun initBottomSheet() {
        bottomSheetDialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottom_sheet_distance, null)
        view.findViewById<ImageView>(R.id.img_delete).setOnClickListener {
            binding.drawView.deleteArrow(true)
            bottomSheetDialog.dismiss()
        }
        view.findViewById<ImageView>(R.id.img_detail).setOnClickListener {
            fetchDataArrow()
            dialogInfo.show()
        }
        view.findViewById<EditText>(R.id.edt_distance).addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {

            }

            override fun onTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {
                val text = p0.toString()
                try {
                    val number = text.toFloat()
                    arrowReceive.valueMeasure = number
                    binding.drawView.drawAgain()
                } catch (e: Exception) {
                    Toast.makeText(
                        requireContext(),
                        "Vui lòng nhập đúng định dạng!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        })
        bottomSheetDialog.setContentView(view)

    }

    private fun initDialogInfo() {
        dialogInfo = Dialog(requireContext())
        dialogInfo.setContentView(bindingDialogInfo.root)

        initListenerDialogInfo(bindingDialogInfo)

        dialogInfo.setCanceledOnTouchOutside(false)
        dialogInfo.setCancelable(false)
        dialogInfo.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun initListenerDialogInfo(b: DialogInfoMaterialBinding) {
        b.cvCancel.setOnClickListener(this)
        b.cvSave.setOnClickListener(this)
        b.cvDelete.setOnClickListener(this)
        b.imgAdd.setOnClickListener(this)
    }

    private fun showDialogTemplate() {
        FragmentDialogTemplate(this).show(parentFragmentManager, "DialogTemplate")
    }

    override fun clickItemTemplate(infoTemplate: InfoTemplate) {
        Toast.makeText(
            requireContext(),
            "Bạn vừa chọn vật liệu: ${infoTemplate.name}",
            Toast.LENGTH_SHORT
        ).show()
        dataSeparationTemplate(infoTemplate.info)
        nameMaterial = infoTemplate.name
        fetchDataNew()
    }

    private fun dataSeparationTemplate(attr: String) {
        if (attr != "") {
            val listAttr = attr.split("-")
            attr1 = listAttr[0]
            attr2 = listAttr[1]
            attr3 = listAttr[2]
        }
    }

    private fun fetchDataNew() {
        bindingDialogInfo.edtName.setText(nameMaterial)
        bindingDialogInfo.edtInfo1.setText(attr1)
        bindingDialogInfo.edtInfo2.setText(attr2)
        bindingDialogInfo.edtInfo3.setText(attr3)
    }

    private fun fetchDataArrow() {
        bindingDialogInfo.edtName.setText(arrowReceive.nameMaterial)
        bindingDialogInfo.edtInfo1.setText(arrowReceive.attribute1)
        bindingDialogInfo.edtInfo2.setText(arrowReceive.attribute2)
        bindingDialogInfo.edtInfo3.setText(arrowReceive.attribute3)
    }

    private fun initFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                arrowViewModel.arrowList.collect { data ->
                    val listArrowDB = data?.arrowList ?: emptyList()
                    val listArrowTmp = listArrowDB.map { it.toArrowDataView() }.toMutableList()

                    binding.drawView.setListArrow(listArrowTmp)
                }
            }
        }
    }

    private fun initViewModel() {
        arrowRepository = ArrowRepository(arrowDao)
        arrowViewModel = ViewModelProvider(
            requireActivity(),
            MaterialFactory(arrowRepository)
        )[MaterialViewModel::class.java]
        arrowViewModel.loadArrows(idPhoto)

        photoRepository= PhotoRepository(photoDao)
        photoViewModel= ViewModelProvider(requireActivity(), PhotoFactory(photoRepository))[PhotoViewModel::class.java]
    }
    private fun savePhotoWithDrawing() {
        // Lấy kích thước view (PhotoView)
        val width = binding.photoRoomDetail.width
        val height = binding.photoRoomDetail.height

        if (width == 0 || height == 0) return

        // Tạo bitmap
        val bitmap = createBitmap(width, height)
        val canvas = Canvas(bitmap)

        // Vẽ PhotoView trước
        binding.photoRoomDetail.draw(canvas)

        // Vẽ DrawView lên trên
        binding.drawView.draw(canvas)

        // Lưu bitmap vào file
        saveBitmapToFile(bitmap)
    }

    private fun saveBitmapToFile(bitmap: Bitmap) {
        val picturesDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val filename = "${idPhoto}_room_with_drawing.png"
        val file = File(picturesDir, filename)

        try {
            // Ghi bitmap vào file
            FileOutputStream(file).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            }

            // Lấy Uri từ FileProvider
            val fileUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                file
            ).buildUpon()
                .appendQueryParameter("t", System.currentTimeMillis().toString())
                .build()

            photoViewModel.updateNewThumbPath(idPhoto, fileUri.toString())
            Log.d("DEBUG", "Lưu thumbpath thành công: $fileUri")
            Toast.makeText(requireContext(), "Lưu ảnh thành công", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Lưu ảnh thất bại", Toast.LENGTH_SHORT).show()
        }
    }

}
