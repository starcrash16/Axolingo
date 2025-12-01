package com.proyecto_final.axolingo.selector_palabras

import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.proyecto_final.axolingo.R

// Clase personalizada para seleccionar palabras y formar respuestas
class ControlSelector : LinearLayout {
    // Elementos de la interfaz
    var txtInstrucciones: TextView? = null // Texto para mostrar instrucciones
    var respuestaContainer: GridLayout? = null // Contenedor para la respuesta del usuario
    var bancoContainer: GridLayout? = null // Contenedor para las opciones disponibles
    var btnRespuesta: Button? = null // Botón para comprobar la respuesta
    var respuestaCorrecta: String? = null // Respuesta correcta esperada

    // Propiedad para establecer instrucciones dinámicamente
    var instrucciones: String = ""
        set(value) {
            field = value
            txtInstrucciones?.text = value
        }

    // Propiedad para establecer la respuesta correcta dinámicamente
    var respuesta: String = ""
        set(value) {
            field = value
            respuestaCorrecta = value
        }

    // Listener para manejar eventos de arrastre y soltar
    private val dragListener = OnDragListener { view, event ->
        val draggedView = event.localState as TextView
        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> { true }
            DragEvent.ACTION_DRAG_ENTERED -> {
                view.setBackgroundColor(Color.rgb(216, 216, 216)) // Cambia el color al entrar
                true
            }
            DragEvent.ACTION_DRAG_EXITED -> {   // Vuelve al color original
                view.setBackgroundResource(android.R.color.transparent)
                true
            }
            DragEvent.ACTION_DROP -> {
                val targetContainer = view as GridLayout
                val originalParent = draggedView.parent as ViewGroup

                if (targetContainer != originalParent) {
                    originalParent.removeView(draggedView)
                    targetContainer.addView(draggedView)
                }
                true
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                draggedView.visibility = View.VISIBLE
                view.setBackgroundResource(android.R.color.transparent)
                true
            }
            else -> false
        }
    }

    // Constructor principal
    constructor(context: Context?) : super(context){
        inicializar(null)
    }

    // Constructor con atributos
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        inicializar(attrs)
    }

    // Constructor con estilo
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        inicializar(attrs)
    }

    // Inicializa los elementos de la vista
    private fun inicializar(attrs: AttributeSet?) {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.selector_palabras, this, true)
        txtInstrucciones = findViewById(R.id.txtInstrucciones)
        respuestaContainer = findViewById(R.id.respuestaContainer)
        bancoContainer = findViewById(R.id.bancoContainer)
        btnRespuesta = findViewById(R.id.btnRespuesta)
        respuestaContainer?.setOnDragListener(dragListener)
        bancoContainer?.setOnDragListener(dragListener)
        val attrs = context.obtainStyledAttributes(attrs, R.styleable.Preguntas)
        txtInstrucciones?.text = attrs.getString(R.styleable.Preguntas_instrucciones)
        respuestaCorrecta = attrs.getString(R.styleable.Preguntas_respuesta)
        attrs.recycle()
    }

    // Carga las palabras en el banco de opciones
    fun cargarBancoDePalabras(palabras: List<String>) {
        bancoContainer?.removeAllViews() // Limpia vistas anteriores

        for (palabra in palabras.shuffled()) { // Desordena las palabras
            val wordView = createWordView(palabra)
            bancoContainer?.addView(wordView)
        }
    }

    // Crea una vista para cada palabra
    fun createWordView(word: String): TextView {
        val wordView = LayoutInflater.from(context).inflate(
            R.layout.recuadro_palabra, bancoContainer, false) as TextView
        wordView.text = word

        // Listener para mover la palabra entre contenedores
        wordView.setOnClickListener {
            val parent = it.parent as ViewGroup
            val clickedWord = it as TextView

            if (parent.id == R.id.bancoContainer) { // Mueve del banco a la respuesta
                bancoContainer?.removeView(clickedWord)
                respuestaContainer?.addView(clickedWord)
            } else if (parent.id == R.id.respuestaContainer) { // Regresa del banco a la respuesta
                respuestaContainer?.removeView(clickedWord)
                bancoContainer?.addView(clickedWord)
            }
        }

        // Listener para arrastrar la palabra
        wordView.setOnLongClickListener { view ->
            val textView = view as TextView
            val item = ClipData.Item(textView.text)
            val dragData = ClipData(textView.text, arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN), item)
            val shadowBuilder = View.DragShadowBuilder(view)
            view.startDragAndDrop(dragData, shadowBuilder, view, 0)
            true
        }
        return wordView
    }

    // Configura el listener para comprobar la respuesta
    fun setComprobarListener(listener: OnClickListener) {
        btnRespuesta?.setOnClickListener(listener)
    }

    // Comprueba si la respuesta del usuario es correcta
    fun comprobarRespuesta() : Boolean {
        val numChildren = respuestaContainer?.childCount ?: return false
        val respuestaFormada = mutableListOf<String>()
        for (i in 0 until numChildren) {
            val wordView = respuestaContainer?.getChildAt(i) as? TextView
            wordView?.text?.let { respuestaFormada.add(it.toString()) }
        }

        val fraseUsuario = respuestaFormada.joinToString(" ")

        return fraseUsuario.equals(respuestaCorrecta, ignoreCase = true)
    }
}