package com.proyecto_final.axolingo.views

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.widget.ImageButton
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.proyecto_final.axolingo.R
import com.proyecto_final.axolingo.art.caroussel.CarouselAdapter
import com.proyecto_final.axolingo.art.caroussel.CarouselItem
import com.proyecto_final.axolingo.menu_vocabulario.MenuVocabularyActivity
import com.proyecto_final.axolingo.selector_palabras.InterfazSelector

class MenuPrincipal @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private lateinit var viewPager: ViewPager2
    private val autoScrollHandler = Handler(Looper.getMainLooper())
    private var autoScrollRunnable: Runnable? = null
    private val AUTO_SCROLL_DELAY_MS = 3000L // 3 segundos

    init {
        // Infla el layout XML y lo adjunta a esta vista.
        inflate(context, R.layout.menu_principal, this)

        // Carga el GIF animado en el ImageView correspondiente.
        val gifImageView: ImageView = findViewById(R.id.gifImageView)
        Glide.with(this)
            .asGif()
            .load(R.drawable.axo_azul_estudioso_nobg) // ¡Recuerda cambiar esto por el nombre de tu GIF!
            .into(gifImageView)

        // Configura toda la funcionalidad del carrusel.
        setupCarousel()
    }

    private fun setupCarousel() {
        viewPager = findViewById(R.id.carouselViewPager)
        val btnLeft: ImageButton = findViewById(R.id.btnCarouselLeft)
        val btnRight: ImageButton = findViewById(R.id.btnCarouselRight)

        // Define los datos que se mostrarán en cada item del carrusel.
        val carouselItems = listOf(
            CarouselItem("Aprende Vocabulario", "Explora nuevas palabras y expande tu conocimiento.", R.drawable.axo_rojo_saltando),
            CarouselItem("Juega y Practica", "Refuerza tu aprendizaje con divertidos minijuegos.", R.drawable.axo_azuk_saltando),
            CarouselItem("Chatea con Axo", "Practica tus habilidades de conversación con nuestro bot.", R.drawable.axo_blanco)
        )

        val carouselItems2 = listOf(
            CarouselItem("Aprende Vocabulario", "Explora nuevas palabras y expande tu conocimiento.", R.drawable.axo_rojo_saltando)
        )

        // Crea el adaptador y le pasa la lógica de navegación.
        // Este bloque de código se "entrega" al adaptador para que lo ejecute al hacer clic.
        val adapter = CarouselAdapter(carouselItems) {
            // La acción a ejecutar: crear un Intent e iniciar la nueva Activity.
            val intent = Intent(context, /*MenuVocabularyActivity::class.java*/ InterfazSelector::class.java)
            context.startActivity(intent)
        }

        viewPager.adapter = adapter


        // Configura los botones de navegación izquierda y derecha del carrusel.
        btnLeft.setOnClickListener {
            viewPager.currentItem = if (viewPager.currentItem > 0) viewPager.currentItem - 1 else carouselItems.size - 1
        }

        btnRight.setOnClickListener {
            viewPager.currentItem = if (viewPager.currentItem < carouselItems.size - 1) viewPager.currentItem + 1 else 0
        }
    }

    private fun startAutoScroll() {
        stopAutoScroll() // Detiene cualquier desplazamiento anterior para evitar duplicados.
        autoScrollRunnable = Runnable {
            val itemCount = viewPager.adapter?.itemCount ?: 0
            if (itemCount > 0) {
                val nextItem = (viewPager.currentItem + 1) % itemCount
                viewPager.setCurrentItem(nextItem, true)
            }
            // Vuelve a programar el siguiente desplazamiento.
            autoScrollHandler.postDelayed(autoScrollRunnable!!, AUTO_SCROLL_DELAY_MS)
        }.also {
            // Inicia el primer desplazamiento.
            autoScrollHandler.postDelayed(it, AUTO_SCROLL_DELAY_MS)
        }
    }

    private fun stopAutoScroll() {
        // Elimina cualquier desplazamiento programado para prevenir fugas de memoria.
        autoScrollRunnable?.let { autoScrollHandler.removeCallbacks(it) }
    }

    // Se llama cuando la vista se adjunta a la ventana (se hace visible).
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAutoScroll()
    }

    // Se llama cuando la vista se desadjunta de la ventana (deja de ser visible).
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAutoScroll()
    }
}

