package com.proyecto_final.axolingo.art.lectura_Art

import android.content.Context
import android.util.AttributeSet
import android.widget.ProgressBar
import androidx.core.content.withStyledAttributes
import com.proyecto_final.axolingo.R

class StageProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.progressBarStyleHorizontal
) : ProgressBar(context, attrs, defStyleAttr) {

    private var totalStages: Int = DEFAULT_TOTAL_STAGES
    private var currentStage: Int = 0

    init {
        isIndeterminate = false
        context.withStyledAttributes(attrs, R.styleable.StageProgressBar) {
            totalStages = getInt(
                R.styleable.StageProgressBar_totalStages,
                DEFAULT_TOTAL_STAGES
            ).coerceAtLeast(1)
            currentStage = getInt(R.styleable.StageProgressBar_currentStage, 0)
        }
        updateMax()
        updateProgressValue()
    }

    fun setTotalStages(total: Int) {
        totalStages = total.coerceAtLeast(1)
        updateMax()
        updateProgressValue()
    }

    fun setStage(stageIndex: Int) {
        currentStage = stageIndex
        updateProgressValue()
    }

    fun getTotalStages(): Int = totalStages

    fun getCurrentStage(): Int = currentStage

    fun advanceToNextStage() {
        setStage(currentStage + 1)
    }

    private fun updateMax() {
        max = totalStages
    }

    private fun updateProgressValue() {
        val normalizedStage = when {
            currentStage < 0 -> 0
            currentStage >= totalStages -> totalStages
            else -> currentStage + 1
        }
        progress = normalizedStage
    }

    companion object {
        private const val DEFAULT_TOTAL_STAGES = 4
    }
}
