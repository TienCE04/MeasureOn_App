import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.example.measure_app.room.entity.DrawingDataArrow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.OutputStream

suspend fun exportHomeRoomArrowExcel(
    context: Context,
    nameHome: String,
    nameRoom: String,
    drawingDataList: List<DrawingDataArrow>
) {
    withContext(Dispatchers.IO) {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Home Room Data")

        // Header
        val header = sheet.createRow(0)
        val headers = listOf("STT", "Tầng", "Tên phòng", "Bộ môn", "Độ to", "Nsx", "Tên đường", "Độ dài")
        headers.forEachIndexed { index, title ->
            header.createCell(index, CellType.STRING).setCellValue(title)
        }

        Log.d("DEBUG",drawingDataList.toString())
        var stt = 1
        var rowIndex = 1

        // Lặp qua danh sách arrow
        drawingDataList.forEach { drawingData ->
            Log.d("DEBUG",drawingData.toString())
            val arrows = drawingData.arrowList
            arrows.forEach { arrow ->
                val row = sheet.createRow(rowIndex++)
                row.createCell(0, CellType.NUMERIC).setCellValue(stt++.toDouble())
                row.createCell(1, CellType.STRING).setCellValue(nameHome) // Tầng
                row.createCell(2, CellType.STRING).setCellValue(nameRoom) // Tên phòng
                row.createCell(3, CellType.STRING).setCellValue(arrow.nameMaterial)
                row.createCell(4, CellType.STRING).setCellValue(arrow.attribute1)
                row.createCell(5, CellType.STRING).setCellValue(arrow.attribute2)
                row.createCell(6, CellType.STRING).setCellValue(arrow.attribute3)
                row.createCell(7, CellType.NUMERIC).setCellValue(arrow.valueMeasure.toDouble())
            }
        }

        // Auto-size
        headers.indices.forEach { sheet.setColumnWidth(it, 20 * 256) }

        // Lưu vào Downloads
        val fileName = "${nameHome}_${nameRoom}_ArrowData.xlsx"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val uri = context.contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
        uri?.let {
            context.contentResolver.openOutputStream(it).use { os: OutputStream? ->
                workbook.write(os)
            }
        }

        workbook.close()
    }
}
