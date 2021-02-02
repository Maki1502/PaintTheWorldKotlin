package edu.ib.painttheworldkotlin

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup.LayoutParams
import kotlin.math.abs

class CanvasView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {


    var params: LayoutParams
    private var path = Path()

    private lateinit var bitmap: Bitmap
    private lateinit var frame: Rect
    private lateinit var extraCanvas: Canvas

    private val brush = Paint().apply {
        isAntiAlias = true
        isDither = true
        color = Color.MAGENTA
        strokeWidth = 8f
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        style = Paint.Style.STROKE

        params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        if (::bitmap.isInitialized) bitmap.recycle()
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(bitmap)
        frame = Rect(0, 0, width, height)

    }

    private var curX = 0f
    private var curY = 0f
    private var pointX = 0f
    private var pointY = 0f

    override fun onTouchEvent(event: MotionEvent): Boolean {

        pointX = event.x
        pointY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN ->
                touchStart()
            MotionEvent.ACTION_MOVE ->
                touchMove()
            MotionEvent.ACTION_UP ->
                touchUp()
        }
        return true
    }

    private fun touchStart(){
        path.reset()
        path.moveTo(pointX, pointY)
        curX = pointX
        curY = pointY
    }

    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop

    private fun touchMove(){
        val dx = abs(pointX - curX)
        val dy = abs(pointY - curY)

        if(dx >= touchTolerance || dy >= touchTolerance){
            path.quadTo(curX, curY, (pointX + curX) / 2, (pointY + curY) / 2)
            curX = pointX
            curY = pointY
            extraCanvas.drawPath(path, brush)
        }
        invalidate()
    }

    private fun touchUp(){
        path.reset()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawBitmap(bitmap, 0f, 0f, null)
        extraCanvas.drawRect(frame, brush)
    }
}