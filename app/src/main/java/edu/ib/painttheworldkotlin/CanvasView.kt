package edu.ib.painttheworldkotlin

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup.LayoutParams
import kotlin.math.abs
import kotlin.properties.Delegates

class CanvasView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var params: LayoutParams
    private var path = Path()

    private lateinit var bitmap: Bitmap
    private lateinit var frame: Rect
    private lateinit var extraCanvas: Canvas

    private var colorName : Int = 0

    private val paintRed = Paint().apply {
        color = Color.RED
    }

    private val paintGreen = Paint().apply {
        color = Color.GREEN
    }

    private val paintBlue = Paint().apply {
        color = Color.BLUE
    }

    private val paintWhite = Paint().apply {
        color = Color.WHITE
    }

    private val paintBlack = Paint().apply {
        color = Color.BLACK
    }

    private val paintMagenta = Paint().apply {
        color = Color.MAGENTA
    }

    /**
     * Okresla wyglad pedzla - plynnosc, wielkosc, ksztalt
     */
    private val brush = Paint().apply {
        isAntiAlias = true
        isDither = true
        strokeWidth = 8f
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        style = Paint.Style.STROKE

        params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

    }

    /**
     * okresla wielkosc okna oraz ramke
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        if (::bitmap.isInitialized) bitmap.recycle()
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(bitmap)
        frame = Rect(0, 180, width, height)

    }

    private var curX = 0f
    private var curY = 0f
    private var pointX = 0f
    private var pointY = 0f

    /**
     * odpowiada za reakcje na dotyk ekranu
     * wywoluje odpowiednie metody
     */
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

    /**
     * w momencie dotkniecia ekranu zapisuje wspolrzedne dotkniecia
     */
    private fun touchStart(){
        if(pointY > 180) {
            path.reset()
            path.moveTo(pointX, pointY)
            curX = pointX
            curY = pointY
        }else{
            path.reset()
        }
    }

    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop

    /**
     * odpowiada za wykrywanie zmiany koloru poprzez najechanie na odpowiedni kwadrat
     * umozliwia rysowanie
     */
    private fun touchMove(){
        val dx = abs(pointX - curX)
        val dy = abs(pointY - curY)

        if(pointY > 180) {
            if (dx >= touchTolerance || dy >= touchTolerance) {
                path.quadTo(curX, curY, (pointX + curX) / 2, (pointY + curY) / 2)
                curX = pointX
                curY = pointY
                extraCanvas.drawPath(path, brush)
            }
        }else if(pointX <= 180){
            path.reset()
            colorName = Color.RED
        }else if(pointX in 190.0..370.0){
            path.reset()
            colorName = Color.GREEN
        }else if(pointX in 380.0..560.0){
            path.reset()
            colorName = Color.BLUE
        }else if(pointX in 570.0..750.0){
            path.reset()
            colorName = Color.WHITE
        }else if(pointX in 760.0..940.0){
            path.reset()
            colorName = Color.BLACK
        }else if(pointX in 950.0..1130.0){
            path.reset()
            colorName = Color.MAGENTA
        }
        invalidate()
    }

    /**
     * odpowiada za przerwanie linii, gdy podniesie sie palec
     */
    private fun touchUp(){
        path.reset()
    }

    /**
     * odpowiada za wyglad ekranu i rozmieszczenie na nim elementow
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawRect(0.0f, 0.0f, 180.0f, 180.0f, paintRed)
        canvas.drawRect(190.0f, 0.0f, 370.0f, 180.0f, paintGreen)
        canvas.drawRect(380.0f, 0.0f, 560.0f, 180.0f, paintBlue)
        canvas.drawRect(570.0f, 0.0f, 750.0f, 180.0f, paintWhite)
        canvas.drawRect(760.0f, 0.0f, 940.0f, 180.0f, paintBlack)
        canvas.drawRect(950.0f, 0.0f, 1130.0f, 180.0f, paintMagenta)

        brush.color = colorName
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        extraCanvas.drawRect(frame, brush)
    }
}