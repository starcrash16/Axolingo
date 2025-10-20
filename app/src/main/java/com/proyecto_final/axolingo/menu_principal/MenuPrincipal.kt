package com.proyecto_final.axolingo.views

import android.content.Context
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
        // Inflamos el nuevo layout que contendrá el ViewPager2 y los botones
        inflate(context, R.layout.menu_principal, this)

        val gifImageView: ImageView = findViewById(R.id.gifImageView)

        Glide.with(this)
            .asGif()
            .load(R.drawable.axo_azul_estudioso_nobg) // <-- 3. ¡CAMBIA ESTO por el nombre de tu archivo GIF!
            .into(gifImageView)
        // --- FIN DEL CÓDIGO PARA EL GIF ---


        setupCarousel()
    }

    private fun setupCarousel() {
        viewPager = findViewById(R.id.carouselViewPager)
        val btnLeft: ImageButton = findViewById(R.id.btnCarouselLeft)
        val btnRight: ImageButton = findViewById(R.id.btnCarouselRight)

        // Aquí creamos los datos de ejemplo para el carrusel.
        // Asegúrate de tener los drawables (ic_book_24, etc.) en tu carpeta res/drawable.
        val carouselItems = listOf(
            CarouselItem("Aprende Vocabulario", "Explora nuevas palabras y expande tu conocimiento.", R.drawable.axo_rojo_saltando),
            CarouselItem("Juega y Practica", "Refuerza tu aprendizaje con divertidos minijuegos.", R.drawable.axo_azuk_saltando),
            CarouselItem("Chatea con Axo", "Practica tus habilidades de conversación con nuestro bot.", R.drawable.axo_blanco)
        )

        viewPager.adapter = CarouselAdapter(carouselItems)

        // Funcionalidad de los botones de navegación
        btnLeft.setOnClickListener {
            viewPager.currentItem = if (viewPager.currentItem > 0) viewPager.currentItem - 1 else carouselItems.size - 1
        }

        btnRight.setOnClickListener {
            viewPager.currentItem = if (viewPager.currentItem < carouselItems.size - 1) viewPager.currentItem + 1 else 0
        }
    }

    private fun startAutoScroll() {
        stopAutoScroll() // Detiene cualquier desplazamiento anterior
        autoScrollRunnable = Runnable {
            val nextItem = if (viewPager.currentItem < (viewPager.adapter?.itemCount ?: 0) - 1) viewPager.currentItem + 1 else 0
            viewPager.setCurrentItem(nextItem, true)
            autoScrollHandler.postDelayed(autoScrollRunnable!!, AUTO_SCROLL_DELAY_MS)
        }.also {
            autoScrollHandler.postDelayed(it, AUTO_SCROLL_DELAY_MS)
        }
    }

    private fun stopAutoScroll() {
        autoScrollRunnable?.let { autoScrollHandler.removeCallbacks(it) }
    }

    // Inicia el auto-scroll cuando la vista es visible
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAutoScroll()
    }

    // Detiene el auto-scroll para prevenir memory leaks cuando la vista no es visible
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAutoScroll()
    }
}

