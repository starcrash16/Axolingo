package com.proyecto_final.axolingo.art.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import com.proyecto_final.axolingo.R

class leyendaMenuBienvenida @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {

    private val strokeColor: Int
    private val strokeWidthValue: Float

    init {
        // Aquí definimos los valores para el borde.
        // Para más flexibilidad en el futuro, estos podrían leerse
        // desde atributos XML personalizados.
        strokeColor = Color.parseColor("#3873b1")
        strokeWidthValue = 8f // Puedes ajustar este valor para un borde más grueso o delgado
        try {
            // Reemplaza 'nombre_de_tu_fuente' con el nombre real de tu archivo en res/font.
            val typeface = ResourcesCompat.getFont(context, R.font.moresugarthinnnn)
            this.typeface = typeface
        } catch (e: Exception) {
            // Manejo de error por si la fuente no se encuentra.
            e.printStackTrace()
        }
    }

    override fun onDraw(canvas: Canvas) {
        val originalColor = currentTextColor

        // Guardamos el estado actual de la "pintura" de texto
        val paintStyle = paint.style
        val paintStrokeWidth = paint.strokeWidth

        // 1. Dibujamos el borde (stroke)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidthValue
        setTextColor(strokeColor)
        super.onDraw(canvas) // Llama al onDraw original para que dibuje el texto con el borde

        // 2. Dibujamos el relleno (fill)
        paint.style = Paint.Style.FILL
        setTextColor(originalColor)
        super.onDraw(canvas) // Llama al onDraw de nuevo para dibujar el texto relleno encima del borde

        // Restauramos la configuración original
        paint.style = paintStyle
        paint.strokeWidth = paintStrokeWidth
    }
}
