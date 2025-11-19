package com.proyecto_final.axolingo.pizarra

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
//import com.google.mlkit.vision.digitalink.Ink
import kotlin.math.abs

class CLienzo : View {
    var extraCanvas: Canvas? = null
    var extraBitmap: Bitmap? = null

    private val STROKE_WIDTH = 12f
    private var backgroundColor: Int = 0
    private var drawColor: Int = 0

    var path: Path = Path()
    var paint: Paint = Paint()
    var text: Paint = Paint()
    //private var inkBuilder = Ink.builder()
    //private var strokeBuilder = Ink.Stroke.builder()

    constructor(context: Context?) : super(context) {
        inicializa()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        inicializa()
    }

    private fun inicializa() {
        //asingacion de colores
        backgroundColor = Color.rgb(173, 173, 173)
        drawColor = Color.BLACK

        paint.color = drawColor
        paint.isAntiAlias = true
        paint.isDither = true
        paint.style = Paint.Style.STROKE
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = STROKE_WIDTH

        //se inicializa el objeto paint para el texto
        text.color = Color.GRAY//Color.argb(255, 100, 100, 100)
        text.textSize = 40f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (extraBitmap != null) {
            canvas.drawBitmap(extraBitmap!!, 0f, 0f, null)
        }
    }

    fun reset() {
        //se libera el bitmap de pintado
        if (extraBitmap != null) {
            extraBitmap!!.recycle()
        }
        //se crea un nuevo Bitmap para pintarlo
        extraBitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap!!)
        extraCanvas!!.drawColor(backgroundColor)
        //inkBuilder = Ink.builder()
        invalidate()
    }

    /*fun getInk(): Ink {
        return inkBuilder.build()
    }*/

    fun getLienzoBitmap(): Bitmap? {
        return extraBitmap
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        reset()
    }

    //posiciones X y Y para pintar
    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f

    override fun onTouchEvent(event: MotionEvent): Boolean {
        //se obtiene X y Y
        motionTouchEventX = event.x
        motionTouchEventY = event.y
        val t = System.currentTimeMillis()
        if (event.action == MotionEvent.ACTION_DOWN) {
            touchStart()//(t)
        }
        if (event.action == MotionEvent.ACTION_MOVE) {
            touchMove()//(t)
        }
        if (event.action == MotionEvent.ACTION_UP) {
            touchUp()//(t)
        }
        return true
    }

    private var currentX = 0f;
    private var currentY = 0f;

    fun touchStart() {//(t: Long) {
        path.reset()
        path.moveTo(motionTouchEventX, motionTouchEventY)
        currentX = motionTouchEventX
        currentY = motionTouchEventY
        //inicia el nuevo trazo de ML Kit
        //strokeBuilder = Ink.Stroke.builder()
        //strokeBuilder.addPoint(Ink.Point.create(motionTouchEventX, motionTouchEventY, t))
    }

    var touchTolerance: Int = ViewConfiguration.get(context).scaledTouchSlop

    fun touchMove() {//(t: Long) {
        val dx = abs((motionTouchEventX - currentX).toDouble()).toFloat()
        val dy = abs((motionTouchEventY - currentY).toDouble()).toFloat()
        if (dx >= touchTolerance || dy >= touchTolerance) {
            path.quadTo(
                currentX,
                currentY,
                (motionTouchEventX + currentX) / 2,
                (motionTouchEventY + currentY) / 2
            )
            currentX = motionTouchEventX
            currentY = motionTouchEventY
            extraCanvas!!.drawPath(path, paint)
            //agrega el punto al trazo de ML Kit
            //strokeBuilder.addPoint(Ink.Point.create(motionTouchEventX, motionTouchEventY, t))
        }
        invalidate()
    }

    fun touchUp() {//(t: Long) {
        path.reset()
        //termina el trazo y lo agrega al "Ink"
        //strokeBuilder.addPoint(Ink.Point.create(motionTouchEventX, motionTouchEventY, t))
        //inkBuilder.addStroke(strokeBuilder.build())
    }
}