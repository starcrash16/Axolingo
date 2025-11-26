package com.proyecto_final.axolingo.leccion_ingles.leccion_ingles

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View

data class MatchedLine(val start: PointF, val end: PointF, val color: Int)

class LineDrawingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var dynamicLine: Triple<PointF, PointF, Int>? = null
    private val matchedLines = mutableListOf<MatchedLine>()
    private val paint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 8f
        isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Draw all permanent matched lines
        matchedLines.forEach { line ->
            paint.color = line.color
            canvas.drawLine(line.start.x, line.start.y, line.end.x, line.end.y, paint)
        }
        // Draw the dynamic line if present
        dynamicLine?.let { (start, end, color) ->
            paint.color = color
            canvas.drawLine(start.x, start.y, end.x, end.y, paint)
        }
    }

    fun setDynamicLine(start: PointF, end: PointF, color: Int) {
        dynamicLine = Triple(start, end, color)
        invalidate()
    }

    fun clearDynamicLine() {
        dynamicLine = null
        invalidate()
    }

    fun addPermanentLine(start: PointF, end: PointF, color: Int) {
        matchedLines.add(MatchedLine(start, end, color))
        invalidate()
    }

    fun clearAllLines() {
        matchedLines.clear()
        dynamicLine = null
        invalidate()
    }
}