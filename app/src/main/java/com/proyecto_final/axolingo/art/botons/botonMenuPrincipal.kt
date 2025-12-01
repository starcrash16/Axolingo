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

    // Constructor básico: se utiliza cuando se crea el botón 
    constructor(context: Context) : super(context) {
        init()
    }

    // Constructor con atributos: se utiliza cuando el botón se define en XML
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    // Constructor con estilo: permite aplicar estilos personalizados desde XML
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    // Método de inicialización: configura el diseño y estilo del botón
    private fun init() {
        // Establecer el texto por defecto si no se ha definido en XML (opcional)
        if (text.isNullOrEmpty()) {
            text = "BOTÓN"
        }

        // Crear el drawable para la sombra (naranja oscuro)
        val shadowDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 50f // Radio de las esquinas
            setColor(Color.parseColor("#fc5a41")) // Color de sombra
        }

        // Crear el drawable para el borde blanco
        val borderDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 50f // El mismo radio de esquinas
            setColor(Color.WHITE) // Color del borde
        }

        // Crear el drawable principal del botón (amarillo anaranjado)
        val mainButtonDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 45f // Un poco menos para que el borde blanco se vea
            setColor(Color.parseColor("#ffde59")) // Color principal del botón
        }

        // Combinar los drawables en un LayerDrawable
        val layerDrawable = LayerDrawable(arrayOf(shadowDrawable, borderDrawable, mainButtonDrawable)).apply {
            // Configurar la posición de cada capa
            setLayerInset(0, 10, 15, 0, 0)      // Sombra: desplazada ligeramente
            setLayerInset(1, 5, 5, 5, 15)       // Borde: más pequeño que la sombra
            setLayerInset(2, 10, 10, 10, 20)    // Principal: más pequeño que el borde
        }

        // Asignar el LayerDrawable como fondo del botón
        background = layerDrawable

        // Configurar el tamaño del texto para que se ajuste automáticamente
        TextViewCompat.setAutoSizeTextTypeWithDefaults(
            this,
            TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM
        )

        // Configurar límites de tamaño del texto
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
            this,
            10, // Tamaño mínimo de texto en SP
            24, // Tamaño máximo de texto en SP
            1,  // Incremento del tamaño
            TypedValue.COMPLEX_UNIT_SP
        )

        // Configurar el color y la fuente del texto
        setTextColor(Color.parseColor("#fc5a41")) // Color del texto
        try {
            val typeface = ResourcesCompat.getFont(context, R.font.moresugarthinnnn)
            this.typeface = typeface
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
