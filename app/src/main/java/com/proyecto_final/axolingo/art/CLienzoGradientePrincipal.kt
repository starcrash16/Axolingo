package com.proyecto_final.axolingo.art

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import com.proyecto_final.axolingo.R


class CLienzoGradientePrincipal : View{
    // Declaramos los Drawables para las imágenes y el gradiente.
    // Se inicializarán en onSizeChanged.
    private var imagen: Drawable? = null
    private var gradientDrawable: GradientDrawable? = null

    // Constructores estándar para una View personalizada.
    // Son necesarios para que Android pueda crear la vista desde un layout XML.
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    /**
     * Este método se llama cuando la vista obtiene su tamaño por primera vez
     * o cuando su tamaño cambia. Es el lugar ideal para inicializar objetos
     * que dependen de las dimensiones de la vista.
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // CREACIÓN DEL GRADIENTE DE FONDO
        val colors = intArrayOf(
            Color.parseColor("#0960fb"),
            Color.parseColor("#0cc0df"),
            Color.parseColor("#ffde59"),
            Color.parseColor("#fc5a41")
        )

        // Creamos la instancia del gradiente
        gradientDrawable = GradientDrawable().apply {
            gradientType = GradientDrawable.RADIAL_GRADIENT
            this.colors = colors
            setGradientCenter(0.0f, 0.0f) // Centro en la esquina superior izquierda (0%, 0%)
            // Usamos la diagonal de la vista como radio para asegurar que el gradiente
            // cubra completamente el lienzo, sin importar su forma.
            gradientRadius = kotlin.math.sqrt((w * w + h * h).toDouble()).toFloat()
        }

        // espacio debe ocupar la gradiente
        gradientDrawable?.setBounds(0, 0, w, h)

        // CARGA Y POSICIONAMIENTO DE IMÁGENES
        imagen = AppCompatResources.getDrawable(context, R.drawable.axo_rojo_saluda)

        // Definir posiciones (x1, y1, x2, y2)
        //imagen?.setBounds(30, 30, 230, 230)  // esquina superior izquierda
        //imagen2?.setBounds(w - 230, 30, w - 30, 230)  // esquina superior derecha
        //imagen3?.setBounds(w / 2 - 100, h / 2 - 100, w / 2 + 100, h / 2 + 100)  // centro
        //imagen4?.setBounds(50, h - 230, 250, h - 30)  // esquina inferior izquierda
        //imagen5?.setBounds(w - 250, h - 230, w - 50, h - 30)  // esquina inferior derecha
    }

    /**
     Este método es el responsable de dibujar todo el contenido de la vista.
     Se llama cada vez que la vista necesita ser redibujada.
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // DIBUJO DEL FONDO
        gradientDrawable?.draw(canvas)

        // DIBUJAR LAS IMÁGENES
        imagen?.draw(canvas)

    }
}
