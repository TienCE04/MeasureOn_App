package com.example.measure_app.service

import android.content.Context
import android.util.Log
import com.example.measure_app.ui.template.data.InfoTemplate
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Sheet
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader

fun mapHeaderToColumnIndex(sheet: Sheet):Map<String,Int>{
    val headerMap=mutableMapOf<String,Int>()

    //đọc tiêu đề cột ở hàng 0 (index 0)

    val headerRow=sheet.getRow(0)?:return headerMap

    headerRow.forEach { cell->
        if(cell.cellType== CellType.STRING){
            val headerName=cell.stringCellValue.trim()
            val columnIndex=cell.columnIndex
            headerMap[headerName]=columnIndex
        }
    }
    return headerMap
}

fun readDataStartingFromRow(context: Context,sheet: Sheet,headerMap: Map<String,Int>){

    val nameMaterial=headerMap["Name"]
    val attr1Column=headerMap["Attr1_Value"]
    val attr2Column=headerMap["Attr2_Value"]
    val attr3Column=headerMap["Attr3_Value"]

    //bắt đầu từ hàng số 2
    for(i in 1 until sheet.physicalNumberOfRows){
        var nameTemplate=""
        var attr1=""
        var attr2=""
        var attr3=""

        val row=sheet.getRow(i)?:continue

        //lấy dữ liệu cho cột cụ thể
        if(nameMaterial!=null){
            val cell=row.getCell(nameMaterial)
            nameTemplate=cell.stringCellValue?:""
        }
        if(attr1Column!=null ){
            val cell=row.getCell(attr1Column)
            attr1=cell.stringCellValue?:""
        }
        if(attr2Column!=null){
            val cell=row.getCell(attr2Column)
            attr2=cell.stringCellValue?:""
        }
        if(attr3Column!=null){
            val cell=row.getCell(attr3Column)
            attr3=cell.stringCellValue?:""
        }

        val itemTemplate= InfoTemplate(nameTemplate,"${attr1}-${attr2}-${attr3}")
        listMaterial.add(itemTemplate)
    }

    saveTemplateListToJson(context,"TemplateFile.json",listMaterial)
}
val listMaterial: MutableList<InfoTemplate> =mutableListOf()

// data class InfoTemplate(val name: String, val attributes: String)
//ghi file TemplateFile.json
fun saveTemplateListToJson(context: Context, fileName: String, dataList: List<InfoTemplate>) {
    val gson = Gson()
    //object->json
    val jsonString = gson.toJson(dataList)

    try {
        // ghi file
        val file = File(context.filesDir, fileName)
        FileOutputStream(file).use {
            it.write(jsonString.toByteArray())
        }
        println("Đã ghi file JSON thành công tại: ${file.absolutePath}")
    } catch (e: Exception) {
        e.printStackTrace()
        // Xử lý lỗi ghi file
    }
}

fun readTemplateListToObject(context: Context,fileName: String): List<InfoTemplate>{
    val gson=Gson()
    val type=object: TypeToken<List<InfoTemplate>>(){}.type
    val file=File(context.filesDir,fileName)

    if(!file.exists()){
        Log.d("DEBUG_GET_FILE_JSON","fail")
        return emptyList()
    }
    try {
        //tạo đối tượng đọc
        val inputStream=file.inputStream()
        //đọc theo bộ đệm
        val reader= BufferedReader(InputStreamReader(inputStream))
        val jsonString=reader.use { it.readText() }

        //từ json->object
        return gson.fromJson(jsonString,type)
    }
    catch (e: Exception){
        e.printStackTrace()
        return emptyList()
    }
}