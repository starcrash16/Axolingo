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

class BotonMenuPrincipal : AppCompatButton {

    // Constructores necesarios para que Android Studio pueda inflar la vista desde XML
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        // Establecer el texto por defecto si no se ha definido en XML (opcional)
        if (text.isNullOrEmpty()) {
            text = "BOTÓN"
        }

        // 1. Crear el drawable para la sombra (naranja oscuro)
        val shadowDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 50f // Radio de las esquinas
            setColor(Color.parseColor("#fc5a41")) // Color de sombra
        }

        // 2. Crear el drawable para el borde blanco
        val borderDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 50f // El mismo radio de esquinas
            setColor(Color.WHITE) // Color del borde
        }

        // 3. Crear el drawable principal del botón (amarillo anaranjado)
        val mainButtonDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 45f // Un poco menos para que el borde blanco se vea
            setColor(Color.parseColor("#ffde59")) // Color principal del botón
        }

        // Crear un LayerDrawable para apilar los elementos
        // El orden es importante: el primero está más abajo, el último está más arriba.
        val layerDrawable = LayerDrawable(arrayOf(shadowDrawable, borderDrawable, mainButtonDrawable)).apply {
            // Desplazar la sombra:
            // left, top, right, bottom
            setLayerInset(0, 10, 15, 0, 0)      // Sombra: La empujamos 15px desde arriba.

            // Ajustar el borde blanco:
            // left, top, right, bottom
            // Para que se vea el borde, lo haremos un poco más pequeño que la sombra,
            // pero más grande que el botón principal.
            setLayerInset(1, 5, 5, 5, 15)       // Borde: Dejamos 15px de espacio abajo.

            // Ajustar el botón principal:
            // left, top, right, bottom
            // Será el más pequeño para que el borde blanco sea visible.
            setLayerInset(2, 10, 10, 10, 20)    // Principal: Dejamos 20px de espacio abajo.
        }

        // Asignar el LayerDrawable como fondo del botón
        background = layerDrawable

        TextViewCompat.setAutoSizeTextTypeWithDefaults(
            this,
            TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM
        )

        // Opcional: Para mayor control, puedes definir los tamaños mínimo, máximo y el paso.
        // La unidad 'SP' es la recomendada para tamaños de fuente.
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
            this,
            10, // Tamaño mínimo de texto en SP
            24, // Tamaño máximo de texto en SP
            1,  // Granularidad del paso (de cuánto en cuánto se reduce)
            TypedValue.COMPLEX_UNIT_SP
        )

        // Personalización del texto (similar a la imagen)
        setTextColor(Color.parseColor("#fc5a41")) // Color del texto
        // Aquí podrías agregar una fuente personalizada si la tuvieras
        try {
            val typeface = ResourcesCompat.getFont(context, R.font.moresugarthinnnn)
            this.typeface = typeface
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
