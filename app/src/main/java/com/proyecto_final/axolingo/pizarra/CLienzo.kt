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
import kotlin.math.abs

// Clase personalizada que representa un lienzo para dibujar
class CLienzo : View {
    // Canvas y Bitmap para el dibujo
    var extraCanvas: Canvas? = null
    var extraBitmap: Bitmap? = null

    private val STROKE_WIDTH = 12f // Ancho del trazo
    private var backgroundColor: Int = 0 // Color de fondo
    private var drawColor: Int = 0 // Color del dibujo

    // Herramientas de dibujo
    var path: Path = Path() // Ruta del dibujo
    var paint: Paint = Paint() // Configuración de pintura
    var text: Paint = Paint() // Configuración de texto

    constructor(context: Context?) : super(context) {
        inicializa() // Inicializa las configuraciones
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        inicializa() // Inicializa las configuraciones
    }

    // Configura los colores y estilos de pintura
    private fun inicializa() {
        backgroundColor = Color.rgb(173, 173, 173) // Color de fondo gris
        drawColor = Color.BLACK // Color de dibujo negro

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

    // Dibuja el contenido del lienzo
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (extraBitmap != null) {
            canvas.drawBitmap(extraBitmap!!, 0f, 0f, null)
        }
    }

    // Reinicia el lienzo
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

    // Devuelve el bitmap actual del lienzo
    fun getLienzoBitmap(): Bitmap? {
        return extraBitmap
    }

    // Ajusta el tamaño del lienzo cuando cambia
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

    // Inicia un nuevo trazo
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

    // Maneja el movimiento del trazo
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

    // Finaliza el trazo
    fun touchUp() {//(t: Long) {
        path.reset()
        //termina el trazo y lo agrega al "Ink"
        //strokeBuilder.addPoint(Ink.Point.create(motionTouchEventX, motionTouchEventY, t))
        //inkBuilder.addStroke(strokeBuilder.build())
    }
}