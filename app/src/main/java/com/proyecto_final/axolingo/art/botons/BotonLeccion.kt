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

// Esta clase define un botón personalizado con estilos avanzados.
// Permite configurar colores principales y de sombra desde XML.
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
        // Colores predeterminados para el botón
        val defaultMainColor = Color.parseColor("#ffde59")
        val defaultShadowColor = Color.parseColor("#fc5a41")

        var mainColor = defaultMainColor
        var shadowColor = defaultShadowColor

        // Leer colores personalizados desde atributos XML
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

        // Crear los estilos del botón (sombra, borde, fondo principal)
        val shadowDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 50f
            setColor(shadowColor)
        }

        // Crear el borde blanco del botón
        val borderDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 50f // Esquinas redondeadas con un radio de 50
            setColor(Color.WHITE) // Color blanco para el borde
        }

        // Crear el fondo principal del botón
        val mainButtonDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 45f // Esquinas redondeadas con un radio de 45
            setColor(mainColor) // Color principal del botón
        }

        // Combinar los estilos en un LayerDrawable
        val layerDrawable = LayerDrawable(arrayOf(shadowDrawable, borderDrawable, mainButtonDrawable)).apply {
            setLayerInset(0, 10, 15, 0, 0)      // Sombra
            setLayerInset(1, 5, 5, 5, 15)       // Borde
            setLayerInset(2, 10, 10, 10, 20)    // Principal
        }

        background = layerDrawable

        // Configurar la fuente del texto del botón
        try {
            val typeface = ResourcesCompat.getFont(context, R.font.moresugarthinnnn)
            this.typeface = typeface
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
