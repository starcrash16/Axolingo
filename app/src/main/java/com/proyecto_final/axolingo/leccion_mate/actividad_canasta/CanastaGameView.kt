package com.proyecto_final.axolingo.leccion_mate.actividad_canasta

import android.content.Context
import android.graphics.*
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.AttributeSet
import android.view.View
import com.proyecto_final.axolingo.R
import kotlin.random.Random

enum class ItemType {
    NUMERO, SIGNO, HUEVO_MALO, HUEVO_BUENO
}

data class FallingItem(
    var x: Float,
    var y: Float,
    val type: ItemType,
    val value: String,
    val bitmap: Bitmap,
    var speed: Float = 8f // Aumenté un poco la velocidad base para que sea más dinámico
)

class CanastaGameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), SensorEventListener {

    interface GameListener {
        fun onItemCaught(item: FallingItem)
        fun onLifeLost()
        fun onGameOver()
    }

    private var gameListener: GameListener? = null
    private val paint = Paint().apply {
        isAntiAlias = true
        textSize = 60f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    // Sensor de acelerómetro
    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null
    private val sensitivity = 15f // Sensibilidad del movimiento

    // Canasta
    private var canastaBitmap: Bitmap? = null
    private var canastaX = 0f
    private var canastaY = 0f
    private val canastaWidth = 200f
    private val canastaHeight = 150f

    // Items que caen
    private val fallingItems = mutableListOf<FallingItem>()
    private var lastSpawnTime = 0L
    private val spawnInterval = 1200L // 1.2 segundos

    // Bitmaps
    private var huevoMaloBitmap: Bitmap? = null
    private var huevoBuenoBitmap: Bitmap? = null

    // Game state
    @Volatile private var isRunning = false // Volatile para seguridad en hilos
    private var isPaused = false
    private var gameThread: Thread? = null

    init {
        loadBitmaps()
        initializeSensors()
        // No iniciamos el loop aquí automáticamente si vamos a usar un menú de inicio
        // startGameLoop()
    }

    private fun initializeSensors() {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    fun startGameLoop() {
        // CORRECCIÓN: Si el hilo existe pero ya murió, necesitamos uno nuevo.
        // Solo retornamos si el hilo existe Y está vivo.
        if (gameThread != null && gameThread!!.isAlive) return

        isRunning = true
        gameThread = Thread {
            while (isRunning) {
                if (!isPaused) {
                    updateGame()
                    postInvalidate() // Pide redibujar en el hilo principal
                    try {
                        Thread.sleep(16) // ~60 FPS
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                } else {
                    try {
                        Thread.sleep(100)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        gameThread?.start()
    }

    private fun loadBitmaps() {
        try {
            val originalCanasta = BitmapFactory.decodeResource(resources, R.drawable.canasta)
            canastaBitmap = Bitmap.createScaledBitmap(originalCanasta, canastaWidth.toInt(), canastaHeight.toInt(), true)

            val originalMalo = BitmapFactory.decodeResource(resources, R.drawable.huevo)
            huevoMaloBitmap = Bitmap.createScaledBitmap(originalMalo, 100, 120, true)

            val originalBueno = BitmapFactory.decodeResource(resources, R.drawable.huevo_bien)
            huevoBuenoBitmap = Bitmap.createScaledBitmap(originalBueno, 100, 120, true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setGameListener(listener: GameListener) {
        this.gameListener = listener
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canastaX = (w - canastaWidth) / 2
        canastaY = h - canastaHeight - 20f
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                // El eje X del acelerómetro: inclinación lateral
                // Valores positivos = inclinado a la derecha
                // Valores negativos = inclinado a la izquierda
                val tiltX = it.values[0]
                
                // Mover la canasta en dirección opuesta a la inclinación
                // (si inclinas a la derecha, la canasta va a la izquierda)
                canastaX -= tiltX * sensitivity
                
                // Mantener la canasta dentro de los límites
                canastaX = canastaX.coerceIn(0f, width - canastaWidth)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No necesitamos implementar esto
    }

    private fun updateGame() {
        // 1. Generar nuevos items
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastSpawnTime > spawnInterval) {
            spawnItem()
            lastSpawnTime = currentTime
        }

        // 2. Actualizar posición y colisiones
        // Usamos iterator para poder remover de forma segura mientras iteramos
        val iterator = fallingItems.iterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            item.y += item.speed

            // Colisión con canasta
            if (checkCollision(item)) {
                gameListener?.onItemCaught(item)
                iterator.remove()
                continue // Saltamos al siguiente item
            }

            // Salió de la pantalla
            if (item.y > height) {
                iterator.remove()
            }
        }
    }

    private fun spawnItem() {
        val itemType = when (Random.nextInt(100)) {
            in 0..35 -> ItemType.NUMERO // 35%
            in 36..55 -> ItemType.SIGNO // 20%
            in 56..80 -> ItemType.HUEVO_MALO // 25% Malos
            else -> ItemType.HUEVO_BUENO // 15% Buenos
        }

        val value = when (itemType) {
            ItemType.NUMERO -> Random.nextInt(0, 10).toString()
            ItemType.SIGNO -> if (Random.nextBoolean()) "+" else "-"
            else -> ""
        }

        // Seguridad por si los bitmaps fallaron al cargar
        if (huevoMaloBitmap == null || huevoBuenoBitmap == null) return

        val bitmap = when (itemType) {
            ItemType.HUEVO_MALO -> huevoMaloBitmap!!
            ItemType.HUEVO_BUENO -> huevoBuenoBitmap!!
            ItemType.NUMERO, ItemType.SIGNO -> createTextBitmap(value)
        }

        val x = Random.nextFloat() * (width - 100f)
        fallingItems.add(FallingItem(x, -150f, itemType, value, bitmap))
    }

    private fun createTextBitmap(text: String): Bitmap {
        val size = 120
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Fondo circular blanco/amarillo
        paint.color = Color.parseColor("#FFF9C4") // Amarillo claro
        canvas.drawCircle(size/2f, size/2f, size/2f - 5, paint)

        // Borde
        paint.color = Color.parseColor("#FBC02D") // Amarillo oscuro
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 8f
        canvas.drawCircle(size/2f, size/2f, size/2f - 5, paint)

        // Texto
        paint.style = Paint.Style.FILL
        paint.color = Color.BLACK
        paint.textSize = 70f
        // Centrar texto verticalmente
        val textHeight = paint.descent() - paint.ascent()
        val textOffset = (textHeight / 2) - paint.descent()
        canvas.drawText(text, size/2f, size/2f + textOffset, paint)

        return bitmap
    }

    private fun checkCollision(item: FallingItem): Boolean {
        // Definir un hitbox más pequeño que la imagen para que se sienta mejor
        val itemCenterX = item.x + (item.bitmap.width / 2)
        val itemBottom = item.y + item.bitmap.height - 20 // Un poco menos de la altura total

        val canastaTop = canastaY + 20 // Un poco más abajo del borde visual
        val canastaLeft = canastaX
        val canastaRight = canastaX + canastaWidth

        // Colisión simple: si el centro inferior del item entra en la caja de la canasta
        return itemBottom >= canastaTop &&
                itemBottom <= canastaTop + 50 && // Solo detectamos colisión en la "boca" de la canasta
                itemCenterX >= canastaLeft &&
                itemCenterX <= canastaRight
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Dibujar items (detrás de la canasta para efecto de profundidad)
        for (item in fallingItems) {
            canvas.drawBitmap(item.bitmap, item.x, item.y, null)
        }

        // Dibujar canasta
        canastaBitmap?.let {
            canvas.drawBitmap(it, canastaX, canastaY, null)
        }
    }

    fun pauseGame() {
        isPaused = true
        unregisterSensor()
    }

    fun resumeGame() {
        isPaused = false
        registerSensor()
    }

    fun stopGame() {
        isRunning = false
        unregisterSensor()
        try {
            gameThread?.join(100) // Esperar a que termine el hilo
        } catch (e: Exception) { e.printStackTrace() }
    }

    fun resetGame() {
        fallingItems.clear()
        lastSpawnTime = 0L
        isPaused = false
        registerSensor()
        // Siempre intentamos iniciar el loop al resetear
        startGameLoop()
    }

    private fun registerSensor() {
        accelerometer?.let {
            sensorManager?.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_GAME
            )
        }
    }

    private fun unregisterSensor() {
        sensorManager?.unregisterListener(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        unregisterSensor()
    }
}