package com.proyecto_final.axolingo.art.botons

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.TextViewCompat
import com.proyecto_final.axolingo.R

class BotonLeccion : AppCompatButton {

    constructor(context: Context) : super(context) {
        init(context, null)
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        // --- 1. Leer colores desde atributos XML ---
        val defaultMainColor = Color.parseColor("#ffde59")
        val defaultShadowColor = Color.parseColor("#fc5a41")

        var mainColor = defaultMainColor
        var shadowColor = defaultShadowColor

        if (attrs != null) {
            val typedArray = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.BotonLeccion,
                0, 0
            )
            try {
                mainColor = typedArray.getColor(R.styleable.BotonLeccion_mainButtonColor, defaultMainColor)
                shadowColor = typedArray.getColor(R.styleable.BotonLeccion_shadowButtonColor, defaultShadowColor)
            } finally {
                typedArray.recycle()
            }
        }

        // --- 2. Crear los Drawables con los colores leídos ---

        // Sombra (Color leído de XML)
        val shadowDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 50f
            setColor(shadowColor) // Usa el color del atributo
        }

        // Borde blanco (Fijo)
        val borderDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 50f
            setColor(Color.WHITE)
        }

        // Botón principal (Color leído de XML)
        val mainButtonDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 45f
            setColor(mainColor) // Usa el color del atributo
        }

        // --- 3. Crear el LayerDrawable (misma lógica de antes) ---
        val layerDrawable = LayerDrawable(arrayOf(shadowDrawable, borderDrawable, mainButtonDrawable)).apply {
            setLayerInset(0, 10, 15, 0, 0)      // Sombra
            setLayerInset(1, 5, 5, 5, 15)       // Borde
            setLayerInset(2, 10, 10, 10, 20)    // Principal
        }

        background = layerDrawable

        // --- 4. Configurar el texto (misma lógica de antes) ---


        // Aplicar la fuente (puedes cambiar esto o quitarlo si no la tienes)
        try {
            val typeface = ResourcesCompat.getFont(context, R.font.moresugarthinnnn)
            this.typeface = typeface
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
