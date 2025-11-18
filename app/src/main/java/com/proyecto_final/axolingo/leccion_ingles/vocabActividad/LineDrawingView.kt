package com.proyecto_final.axolingo.leccion_ingles.leccion_ingles


import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.graphics.PointF

/**
 * Custom View used to draw the connection lines between buttons.
 * It holds a list of matched lines and one temporary line being drawn by the user.
 */
class LineDrawingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Data class to hold a permanent, correctly matched line
    data class MatchedLine(val start: PointF, val end: PointF, val color: Int)

    // Variables for drawing
    private val paint = Paint().apply {
        color = Color.parseColor("#4A148C") // Purple color for lines
        strokeWidth = 10f
        isAntiAlias = true
    }

    // List of permanent, correct lines
    private val matchedLines = mutableListOf<MatchedLine>()

    // Temporary line currently being drawn by user (start point and current finger position)
    var tempLineStart: PointF? = null
    var tempLineEnd: PointF? = null

    // Adds a validated line to the permanent collection
    fun addMatchedLine(start: PointF, end: PointF, color: Int) {
        matchedLines.add(MatchedLine(start, end, color))
        tempLineStart = null
        tempLineEnd = null
        invalidate() // Redraw the view
    }

    // Clears the temporary line (e.g., if connection failed or was released)
    fun clearTempLine() {
        tempLineStart = null
        tempLineEnd = null
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw all permanent matched lines
        matchedLines.forEach { line ->
            paint.color = line.color // Use the line's color
            canvas.drawLine(line.start.x, line.start.y, line.end.x, line.end.y, paint)
        }

        // Draw the temporary line the user is currently dragging
        tempLineStart?.let { start ->
            tempLineEnd?.let { end ->
                paint.color = Color.parseColor("#FF5722") // Orange color for temp line
                canvas.drawLine(start.x, start.y, end.x, end.y, paint)
            }
        }
    }
}