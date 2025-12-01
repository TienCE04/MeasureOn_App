package com.example.measure_app.canvas.arrow

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.*
import com.example.measure_app.room.entity.DrawingDataArrow
import com.example.measure_app.util.convert.toDataArrow
import com.github.chrisbanes.photoview.PhotoView
import kotlin.math.*

class DrawView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    enum class Mode { ARROW, AREA, ANGLE, TEXT, DRAW, NULL }

    var onClickDrawListener: OnClickDraw? = null
    private var mode: Mode = Mode.NULL
    private val arrows = mutableListOf<ArrowDataView>()
    private var selectedArrow: ArrowDataView? = null
    private val controlRadius = 40f

    // Touch state
    private var movingPoint: String? = null
    private var isMovingBody = false
    private var startX = 0f // Dùng để lưu tọa độ X của lần chạm/di chuyển trước
    private var startY = 0f // Dùng để lưu tọa độ Y của lần chạm/di chuyển trước
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop

    // Biến trạng thái để kiểm soát việc tiêu thụ sự kiện
    private var isDrawingOrInteracting = false

    // PhotoView reference
    private var photoView: PhotoView? = null

    private val defaultColor = Color.RED
    private val defaultStrokeWidth = 5f

    private val paintCircle = Paint().apply {
        color = Color.BLUE
        alpha = 100
        style = Paint.Style.FILL
    }

    // Long press detector (dùng cho việc chọn mũi tên và mở Bottom Sheet)
    private var longPressDetector =
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onLongPress(e: MotionEvent) {
                // Chỉ xử lý Long Press nếu không ở chế độ Multitouch
                if (e.pointerCount > 1) return

                val (x, y) = mapTouchToImage(e)

                // Tìm và chọn mũi tên
                arrows.find { isNearLine(it, x, y) }?.let { arrowNearLine ->
                    unselectAllAndHideBottomSheet() // Bỏ chọn cái cũ
                    selectedArrow = arrowNearLine
                    arrowNearLine.isSelected = true

                    // Mở Bottom Sheet
                    onClickDrawListener?.onArrowSelected(arrowNearLine)

                    // Sau Long Press, ta coi như tương tác đã kết thúc
                    isDrawingOrInteracting = false
                    invalidate()

                }
            }

            // Thêm onDown để đảm bảo GestureDetector hoạt động đúng
            override fun onDown(e: MotionEvent): Boolean {
                // Trả về true để LongPressDetector theo dõi các sự kiện tiếp theo
                return true
            }

            // Thêm onSingleTapUp để xử lý thao tác click/chọn đơn giản nếu cần
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                // Khi người dùng click nhanh (chạm-nhấc)
                val (x, y) = mapTouchToImage(e)

                val arrowNearLine = arrows.find { isNearLine(it, x, y) }

                if (arrowNearLine != null) {
                    // Click vào mũi tên -> Chọn (nhưng không mở BottomSheet, chỉ LongPress mới mở)
                    unselectAllAndHideBottomSheet()
                    selectedArrow = arrowNearLine
                    selectedArrow!!.isSelected = true
                    invalidate()
                    // Tiêu thụ sự kiện để PhotoView không xử lý SingleTap
                    return true
                } else {
                    // Click không trúng -> Bỏ chọn tất cả
                    unselectAllAndHideBottomSheet()
                    // Không tiêu thụ sự kiện. Cho phép PhotoView xử lý (ví dụ: Double tap zoom)
                    return false
                }
            }
        })

    fun setPhotoView(pv: PhotoView) {
        photoView = pv
    }

    fun setMode(newMode: Mode) {
        mode = newMode
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // ... (Giữ nguyên logic vẽ)
        arrows.forEach { arrow ->
            canvas.drawLine(arrow.startX, arrow.startY, arrow.endX, arrow.endY, arrow.paint)
            drawArrowHead(canvas, arrow)
            drawArrowInfoBox(canvas, arrow)

            if (arrow.isSelected) {
                canvas.drawCircle(arrow.startX, arrow.startY, controlRadius, paintCircle)
                canvas.drawCircle(arrow.endX, arrow.endY, controlRadius, paintCircle)
            }
        }
    }

    fun unselectAllAndHideBottomSheet() {
        arrows.forEach { it.isSelected = false }
        selectedArrow = null
        onClickDrawListener?.onSelectionCleared()
        invalidate()
    }

    private fun drawArrowHead(canvas: Canvas, arrow: ArrowDataView) {
        val angle = atan2((arrow.endY - arrow.startY), (arrow.endX - arrow.startX))
        val arrowLength = 30f
        val arrowAngle = Math.toRadians(30.0)

        val x1 = arrow.endX - arrowLength * cos(angle - arrowAngle).toFloat()
        val y1 = arrow.endY - arrowLength * sin(angle - arrowAngle).toFloat()
        val x2 = arrow.endX - arrowLength * cos(angle + arrowAngle).toFloat()
        val y2 = arrow.endY - arrowLength * sin(angle + arrowAngle).toFloat()

        canvas.drawLine(arrow.endX, arrow.endY, x1, y1, arrow.paint)
        canvas.drawLine(arrow.endX, arrow.endY, x2, y2, arrow.paint)
    }

    // Giữ nguyên drawArrowInfoBox (cần đảm bảo nó được định nghĩa ở đây)

    private fun mapTouchToImage(event: MotionEvent): Pair<Float, Float> {
        val pv = photoView ?: return Pair(event.x, event.y)
        val inverse = Matrix()
        pv.imageMatrix.invert(inverse)
        val touchPoint = floatArrayOf(event.x, event.y)
        inverse.mapPoints(touchPoint)
        return Pair(touchPoint[0], touchPoint[1])
    }

    private fun distance(x1: Float, y1: Float, x2: Float, y2: Float) =
        sqrt((x1 - x2).pow(2) + (y1 - y2).pow(2))

    private fun distanceToSegment(
        px: Float,
        py: Float,
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float
    ): Float {
        val dx = x2 - x1
        val dy = y2 - y1
        if (dx == 0f && dy == 0f) return distance(px, py, x1, y1)
        val t = ((px - x1) * dx + (py - y1) * dy) / (dx * dx + dy * dy)
        return when {
            t < 0 -> distance(px, py, x1, y1)
            t > 1 -> distance(px, py, x2, y2)
            else -> distance(px, py, x1 + t * dx, y1 + t * dy)
        }
    }

    private fun isNearLine(arrow: ArrowDataView, x: Float, y: Float): Boolean {
        val dist = distanceToSegment(x, y, arrow.startX, arrow.startY, arrow.endX, arrow.endY)
        return dist < controlRadius * 2
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {

        // --- 1. Xử lý Multitouch (Pinch Zoom) ---
        // Nếu có 2 ngón tay trở lên, DrawView phải trả về false để nhường quyền cho PhotoView.
        if (event.pointerCount > 1) {
            // Hủy bỏ mọi trạng thái tương tác đơn ngón tay đang diễn ra
            isDrawingOrInteracting = false
            movingPoint = null
            isMovingBody = false
            return false // Nhường quyền xử lý
        }

        // --- 2. Xử lý Single Touch (Vẽ/Kéo/LongPress) ---

        // Xử lý GestureDetector cho Long Press/Single Tap.
        val consumedByGestureDetector = longPressDetector.onTouchEvent(event)

        // Ánh xạ tọa độ chạm
        val (x, y) = mapTouchToImage(event)

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                startX = x // Lưu tọa độ chạm ban đầu
                startY = y
                movingPoint = null
                isMovingBody = false
                isDrawingOrInteracting = false // Đặt lại

                var isInteractingWithArrow =
                    false // Cờ kiểm tra liệu DrawView có nên tiêu thụ sự kiện

                selectedArrow?.let { arrow ->
                    // 1. Kiểm tra điểm điều khiển (luôn ưu tiên)
                    val isStart = distance(x, y, arrow.startX, arrow.startY) < controlRadius * 2
                    val isEnd = distance(x, y, arrow.endX, arrow.endY) < controlRadius * 2
                    if (isStart || isEnd) {
                        movingPoint = if (isStart) "start" else "end"
                        isDrawingOrInteracting = true // Kéo điểm điều khiển
                        isInteractingWithArrow = true
                        // Nếu đang kéo điểm điều khiển, phải tiêu thụ sự kiện
                        invalidate()
                        return true
                    }
                }

                val arrowNearLine = arrows.find { isNearLine(it, x, y) }
                if (arrowNearLine != null) {
                    // 2. Chạm vào thân (sẵn sàng cho Drag/LongPress)
                    unselectAllAndHideBottomSheet()
                    selectedArrow = arrowNearLine
                    arrowNearLine.isSelected = true
                    isMovingBody = true
                    isDrawingOrInteracting = true
                    isInteractingWithArrow = true
                }

                if (mode == Mode.ARROW && !isInteractingWithArrow) {
                    // 3. Chế độ vẽ mới (chỉ xảy ra nếu không chạm vào mũi tên nào)
                    val newArrow = ArrowDataView(
                        startX = x, startY = y, endX = x, endY = y,
                        color = defaultColor, strokeWidth = defaultStrokeWidth, isSelected = true
                    )
                    arrows.add(newArrow)
                    selectedArrow = newArrow
                    isDrawingOrInteracting = true // Đang tương tác vẽ mới
                    isInteractingWithArrow = true
                }

                invalidate()

                // QUY TẮC TIÊU THỤ:
                // Trả về TRUE chỉ khi DrawView bắt đầu tương tác với mũi tên (vẽ, kéo điểm, chờ kéo thân).
                // Nếu DrawView không tương tác (mode=NULL và chạm vùng trống) -> return FALSE để PhotoView xử lý Pan/Zoom.
                return isInteractingWithArrow
            }

            MotionEvent.ACTION_MOVE -> {
                // Chỉ xử lý MOVE nếu đã bắt đầu tương tác từ ACTION_DOWN
                // Nếu đang di chuyển thân, ta luôn muốn xử lý (isDrawingOrInteracting đã được set)
                if (!isDrawingOrInteracting) return false

                val dx = x - startX // Độ dịch chuyển X so với lần trước
                val dy = y - startY // Độ dịch chuyển Y so với lần trước
                val movedDistance = hypot(dx, dy)

                // Nếu di chuyển quá touchSlop, xác nhận đây là thao tác kéo
                if (movedDistance > touchSlop) {
                    isDrawingOrInteracting = true // Xác nhận là kéo
                }

                if (movingPoint != null) {
                    // Kéo điểm điều khiển
                    selectedArrow?.let {
                        if (movingPoint == "start") {
                            it.startX = x
                            it.startY = y
                        } else {
                            it.endX = x
                            it.endY = y
                        }
                    }
                } else if (isMovingBody && isDrawingOrInteracting) {
                    // *** KHẮC PHỤC LỖI Ở ĐÂY ***
                    // Di chuyển thân mũi tên bằng độ dịch chuyển incremental (dx, dy)
                    selectedArrow?.let {
                        it.startX += dx
                        it.startY += dy
                        it.endX += dx
                        it.endY += dy
                    }
                    // Cập nhật startX, startY thành tọa độ hiện tại (x, y)
                    // để lần MOVE tiếp theo tính toán độ dịch chuyển từ đây.
                    startX = x
                    startY = y
                } else if (mode == Mode.ARROW && selectedArrow != null) {
                    // Kéo mũi tên mới
                    selectedArrow?.endX = x
                    selectedArrow?.endY = y
                }

                invalidate()
                return true
            }

            MotionEvent.ACTION_UP -> {
                val wasInteracting = isDrawingOrInteracting
                isDrawingOrInteracting = false
                movingPoint = null
                isMovingBody = false

                // Nếu LongPressDetector đã tiêu thụ sự kiện (LongPress đã xảy ra), ta không làm gì thêm
                if (consumedByGestureDetector) return true

                // Trả về true nếu DrawView đã thực hiện một thao tác kéo/vẽ,
                // ngược lại trả về false để PhotoView xử lý Click đơn (SingleTap) hoặc các gesture khác.
                return wasInteracting
            }
        }

        // Trả về kết quả của LongPressDetector.
        // Nếu không có tương tác nào, ta để PhotoView xử lý.
        return isDrawingOrInteracting || consumedByGestureDetector
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    fun deleteArrow(isSelected: Boolean) {
        // Cần đảm bảo logic xóa của bạn là đúng (dựa trên isSelected)
        val iterator = arrows.iterator()
        while (iterator.hasNext()) {
            val arrow = iterator.next()
            if (arrow.isSelected) {
                iterator.remove()
                break // Chỉ xóa một mũi tên được chọn
            }
        }
        selectedArrow = null
        onClickDrawListener?.onSelectionCleared()
        invalidate()
    }

    fun drawAgain() {
        invalidate()
    }

    fun setListArrow(list: List<ArrowDataView>) {
        arrows.clear()
        arrows.addAll(list)
        Log.d("DEBUG",list.toString())
        invalidate()
    }

    fun convertToDB(idPhoto: Int): DrawingDataArrow {
        val listTmp = mutableListOf<DataArrow>()
        for (i in 0..<arrows.size) {
            listTmp.add(arrows[i].toDataArrow())
        }
        return DrawingDataArrow(idPhoto, listTmp)
    }
}

interface OnClickDraw {
    fun onArrowSelected(arrow: ArrowDataView)
    fun onSelectionCleared()
}