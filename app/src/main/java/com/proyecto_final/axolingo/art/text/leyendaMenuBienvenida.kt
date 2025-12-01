package com.proyecto_final.axolingo.art.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import com.proyecto_final.axolingo.R

// Clase personalizada para un TextView con borde y fuente específica
class leyendaMenuBienvenida @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {

    private val strokeColor: Int // Color del borde del texto
    private val strokeWidthValue: Float // Grosor del borde

    init {
        // Inicialización de los valores del borde
        strokeColor = Color.parseColor("#3873b1") // Color azul para el borde
        strokeWidthValue = 8f // Grosor del borde
        try {
            // Configuración de la fuente personalizada
            val typeface = ResourcesCompat.getFont(context, R.font.moresugarthinnnn)
            this.typeface = typeface
        } catch (e: Exception) {
            // Manejo de errores si la fuente no se encuentra
            e.printStackTrace()
        }
    }

    // Método para dibujar el texto con borde y relleno
    override fun onDraw(canvas: Canvas) {
        val originalColor = currentTextColor // Guardar el color original del texto

        // Guardar el estado actual de la pintura del texto
        val paintStyle = paint.style
        val paintStrokeWidth = paint.strokeWidth

        // Dibujar el borde del texto
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidthValue
        setTextColor(strokeColor)
        super.onDraw(canvas) // Dibujar el texto con el borde

        // Dibujar el relleno del texto
        paint.style = Paint.Style.FILL
        setTextColor(originalColor)
        super.onDraw(canvas) // Dibujar el texto relleno encima del borde

        // Restaurar la configuración original de la pintura
        paint.style = paintStyle
        paint.strokeWidth = paintStrokeWidth
    }
}
