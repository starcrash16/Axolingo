package com.proyecto_final.axolingo.selector_palabras

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.proyecto_final.axolingo.R

class ControlSelector : LinearLayout {
    var txtInstrucciones: TextView? = null
    var respuestaContainer: GridLayout? = null
    var bancoContainer: GridLayout? = null
    var btnRespuesta: Button? = null
    var respuestaCorrecta: String? = null

    var instrucciones: String = ""
        set(value) {
            field = value
            txtInstrucciones?.text = value
        }

    var respuesta: String = ""
        set(value) {
            field = value
            respuestaCorrecta = value
        }

    constructor(context: Context?) : super(context){
        inicializar(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        inicializar(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        inicializar(attrs)
    }

    private fun inicializar(attrs: AttributeSet?) {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.selector_palabras, this, true)
        txtInstrucciones = findViewById(R.id.txtInstrucciones)
        respuestaContainer = findViewById(R.id.respuestaContainer)
        bancoContainer = findViewById(R.id.bancoContainer)
        btnRespuesta = findViewById(R.id.btnRespuesta)
        val attrs = context.obtainStyledAttributes(attrs, R.styleable.ControlSelector)
        txtInstrucciones?.text = attrs.getString(R.styleable.ControlSelector_instrucciones)
        respuestaCorrecta = attrs.getString(R.styleable.ControlSelector_respuesta)
        attrs.recycle()
    }

    fun cargarBancoDePalabras(palabras: List<String>) {
        bancoContainer?.removeAllViews()       //limpia vistas creadas anteriormente

        for (palabra in palabras.shuffled()) { //shuffle desordena las palabras
            val wordView = createWordView(palabra)
            bancoContainer?.addView(wordView)
        }
    }

    fun createWordView(word: String): TextView {
        val wordView = LayoutInflater.from(context).inflate(
            R.layout.recuadro_palabra, bancoContainer, false) as TextView
        wordView.text = word

        //listener de clic para mover la palabra
        wordView.setOnClickListener {
            val parent = it.parent as ViewGroup
            val clickedWord = it as TextView

            if (parent.id == R.id.bancoContainer) {     //mueve la palabra del banco a la respuesta
                bancoContainer?.removeView(clickedWord)
                respuestaContainer?.addView(clickedWord)
            } else if (parent.id == R.id.respuestaContainer) {     //regresa la palabra de la respuesta al banco
                respuestaContainer?.removeView(clickedWord)
                bancoContainer?.addView(clickedWord)
            }
        }
        return wordView
    }

    fun setComprobarListener(listener: OnClickListener) {
        btnRespuesta?.setOnClickListener(listener)
    }

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