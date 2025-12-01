package com.proyecto_final.axolingo.art.lectura_Art

import android.content.Context
import android.util.AttributeSet
import android.widget.ProgressBar
import androidx.core.content.withStyledAttributes
import com.proyecto_final.axolingo.R

// Barra de progreso personalizada para representar etapas o niveles
class StageProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.progressBarStyleHorizontal
) : ProgressBar(context, attrs, defStyleAttr) {

    private var totalStages: Int = DEFAULT_TOTAL_STAGES // Número total de etapas
    private var currentStage: Int = 0 // Etapa actual

    init {
        isIndeterminate = false // La barra no es indeterminada
        // Leer atributos personalizados desde XML
        context.withStyledAttributes(attrs, R.styleable.StageProgressBar) {
            totalStages = getInt(
                R.styleable.StageProgressBar_totalStages,
                DEFAULT_TOTAL_STAGES
            ).coerceAtLeast(1) // Asegurar que haya al menos 1 etapa
            currentStage = getInt(R.styleable.StageProgressBar_currentStage, 0)
        }
        updateMax() // Configurar el valor máximo de la barra
        updateProgressValue() // Actualizar el progreso inicial
    }

    // Establece el número total de etapas
    fun setTotalStages(total: Int) {
        totalStages = total.coerceAtLeast(1)
        updateMax()
        updateProgressValue()
    }

    // Establece la etapa actual
    fun setStage(stageIndex: Int) {
        currentStage = stageIndex
        updateProgressValue()
    }

    // Devuelve el número total de etapas
    fun getTotalStages(): Int = totalStages

    // Devuelve la etapa actual
    fun getCurrentStage(): Int = currentStage

    // Avanza a la siguiente etapa
    fun advanceToNextStage() {
        setStage(currentStage + 1)
    }

    // Configura el valor máximo de la barra basado en el número total de etapas
    private fun updateMax() {
        max = totalStages
    }

    // Actualiza el progreso de la barra basado en la etapa actual
    private fun updateProgressValue() {
        val normalizedStage = when {
            currentStage < 0 -> 0 // No puede ser menor que 0
            currentStage >= totalStages -> totalStages // No puede exceder el total
            else -> currentStage + 1 // Ajustar para que sea 1-indexado
        }
        progress = normalizedStage
    }

    companion object {
        private const val DEFAULT_TOTAL_STAGES = 4 // Valor predeterminado de etapas
    }
}
