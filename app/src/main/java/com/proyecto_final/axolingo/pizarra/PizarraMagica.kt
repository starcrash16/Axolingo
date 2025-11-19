package com.proyecto_final.axolingo.pizarra

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
import android.widget.Toast
import com.google.mlkit.vision.common.InputImage
/*import com.google.mlkit.vision.digitalink.DigitalInkRecognition
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModel
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModelIdentifier
import com.google.mlkit.vision.digitalink.DigitalInkRecognizer
import com.google.mlkit.vision.digitalink.DigitalInkRecognizerOptions*/
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.proyecto_final.axolingo.R

class PizarraMagica : LinearLayout {
    var txtInstrucciones: TextView? = null
    var respuestaContainer: GridLayout? = null
    var bancoContainer: GridLayout? = null
    var btnRespuesta: Button? = null
    var respuestaCorrecta: String? = null
    private var lienzo: CLienzo? = null
    private var btnBorrar: Button? = null
    private var btnLeer: Button? = null
    private var txtNumero: TextView? = null
    var numeroDetectado: String = ""
    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    //private val digitalInkRecognizer: DigitalInkRecognizer

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

    private val dragListener = OnDragListener { view, event ->
        val draggedView = event.localState as TextView
        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> { true }
            DragEvent.ACTION_DRAG_ENTERED -> {
                view.setBackgroundColor(Color.rgb(216, 216, 216))
                true
            }
            DragEvent.ACTION_DRAG_EXITED -> {   //volver al valor original
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

    constructor(context: Context?) : super(context){
        /*// 1. Obtén el IDENTIFICADOR (el nombre)
        val modelIdentifier = DigitalInkRecognitionModelIdentifier.fromLanguageTag("zxx-Zsym-t-i0-und")!!
        // 2. Construye el MODELO (el objeto) usando el identificador
        val model = DigitalInkRecognitionModel.builder(modelIdentifier).build()
        // 3. Construye las OPCIONES usando el modelo
        val options = DigitalInkRecognizerOptions.builder(model).build()
        // 4. Obtén el CLIENTE con esas opciones
        digitalInkRecognizer = DigitalInkRecognition.getClient(options)*/
        inicializar(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        /*// 1. Obtén el IDENTIFICADOR (el nombre)
        val modelIdentifier = DigitalInkRecognitionModelIdentifier.fromLanguageTag("zxx-Zsym-t-i0-und")!!
        // 2. Construye el MODELO (el objeto) usando el identificador
        val model = DigitalInkRecognitionModel.builder(modelIdentifier).build()
        // 3. Construye las OPCIONES usando el modelo
        val options = DigitalInkRecognizerOptions.builder(model).build()
        // 4. Obtén el CLIENTE con esas opciones
        digitalInkRecognizer = DigitalInkRecognition.getClient(options)*/
        inicializar(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        /*// 1. Obtén el IDENTIFICADOR (el nombre)
        val modelIdentifier = DigitalInkRecognitionModelIdentifier.fromLanguageTag("zxx-Zsym-t-i0-und")!!
        // 2. Construye el MODELO (el objeto) usando el identificador
        val model = DigitalInkRecognitionModel.builder(modelIdentifier).build()
        // 3. Construye las OPCIONES usando el modelo
        val options = DigitalInkRecognizerOptions.builder(model).build()
        // 4. Obtén el CLIENTE con esas opciones
        digitalInkRecognizer = DigitalInkRecognition.getClient(options)*/
        inicializar(attrs)
    }

    private fun inicializar(attrs: AttributeSet?) {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.selector_pizzarra, this, true)
        txtInstrucciones = findViewById(R.id.txtInstrucciones)
        respuestaContainer = findViewById(R.id.respuestaContainer)
        bancoContainer = findViewById(R.id.bancoContainer)
        btnRespuesta = findViewById(R.id.btnRespuesta)
        respuestaContainer?.setOnDragListener(dragListener)
        bancoContainer?.setOnDragListener(dragListener)
        btnBorrar = findViewById(R.id.btnBorrar)
        lienzo = findViewById(R.id.lienzo)
        btnLeer = findViewById(R.id.btnLeer)
        txtNumero = findViewById(R.id.txtNumero)
        btnBorrar?.setOnClickListener {
            lienzo?.reset()
            txtNumero?.text = "Numero: "
        }
        btnLeer?.setOnClickListener {
            reconocerNumeroDelLienzo()
        }
        val attrs = context.obtainStyledAttributes(attrs, R.styleable.Preguntas)
        txtInstrucciones?.text = attrs.getString(R.styleable.Preguntas_instrucciones)
        respuestaCorrecta = attrs.getString(R.styleable.Preguntas_respuesta)
        attrs.recycle()
    }

    fun cargarBancoDePalabras(palabras: List<Int>) {
        bancoContainer?.removeAllViews()       //limpia vistas creadas anteriormente

        for (palabra in palabras.shuffled()) { //shuffle desordena las palabras
            val wordView = createWordView(palabra.toString())
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

        //listener para arrastrar la palabra
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

    private fun reconocerNumeroDelLienzo() {
        val bitmap = lienzo?.getLienzoBitmap()
        if (bitmap == null) {
            Toast.makeText(context, "Error: Lienzo no disponible", Toast.LENGTH_SHORT).show()
            return
        }

        val image = InputImage.fromBitmap(bitmap, 0)

        textRecognizer.process(image)
            .addOnSuccessListener { visionText ->
                if (visionText.textBlocks.isEmpty()) {
                    txtNumero?.text = "Numero: ?"
                } else {
                    numeroDetectado = visionText.text.trim()
                    txtNumero?.text = "Numero: $numeroDetectado"
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al leer", Toast.LENGTH_SHORT).show()
            }

        /*
        //obtiene el objeto INK
        val ink = lienzo?.getInk()
        if (ink == null || ink.strokes.isEmpty()) {
            Toast.makeText(context, "Dibuja un número primero", Toast.LENGTH_SHORT).show()
            return
        }

        //procesa el INK de forma asincrona
        digitalInkRecognizer.recognize(ink)
            .addOnSuccessListener { result ->
                //procesa el resultado
                if (result.candidates.isEmpty()) {
                    txtNumero?.text = "Numero: ?"
                } else {
                    val numeroDetectado = result.candidates[0].text
                    txtNumero?.text = "Numero: $numeroDetectado"
                }
            }
            .addOnFailureListener { e -> //mensaje de error
                Toast.makeText(context, "Error al leer: ${e.message}", Toast.LENGTH_SHORT).show()
            }

         */
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

        if (numeroDetectado.isEmpty()) {

            val fraseUsuario = respuestaFormada.joinToString(" ")

            return fraseUsuario.equals(respuestaCorrecta, ignoreCase = true)
        } else {
            return numeroDetectado.equals(respuestaCorrecta, ignoreCase = true)
        }
    }
}