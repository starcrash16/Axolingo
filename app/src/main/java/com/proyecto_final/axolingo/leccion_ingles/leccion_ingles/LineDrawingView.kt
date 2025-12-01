package com.proyecto_final.axolingo.leccion_ingles.leccion_ingles

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View

// Clase personalizada para dibujar líneas dinámicas y permanentes en un lienzo
// Utilizada para actividades interactivas en la lección de inglés

data class MatchedLine(val start: PointF, val end: PointF, val color: Int) // Representa una línea permanente con color

class LineDrawingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var dynamicLine: Triple<PointF, PointF, Int>? = null // Línea dinámica que se dibuja temporalmente
    private val matchedLines = mutableListOf<MatchedLine>() // Lista de líneas permanentes
    private val paint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 8f
        isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Dibujar todas las líneas permanentes
        matchedLines.forEach { line ->
            paint.color = line.color
            canvas.drawLine(line.start.x, line.start.y, line.end.x, line.end.y, paint)
        }
        // Dibujar la línea dinámica si está presente
        dynamicLine?.let { (start, end, color) ->
            paint.color = color
            canvas.drawLine(start.x, start.y, end.x, end.y, paint)
        }
    }

    // Establecer una línea dinámica temporal
    fun setDynamicLine(start: PointF, end: PointF, color: Int) {
        dynamicLine = Triple(start, end, color)
        invalidate()
    }

    // Limpiar la línea dinámica
    fun clearDynamicLine() {
        dynamicLine = null
        invalidate()
    }

    // Agregar una línea permanente al lienzo
    fun addPermanentLine(start: PointF, end: PointF, color: Int) {
        matchedLines.add(MatchedLine(start, end, color))
        invalidate()
    }

    // Limpiar todas las líneas (dinámicas y permanentes)
    fun clearAllLines() {
        matchedLines.clear()
        dynamicLine = null
        invalidate()
    }
}