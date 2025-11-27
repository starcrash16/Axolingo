package com.proyecto_final.axolingo.views

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.widget.ImageButton
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.proyecto_final.axolingo.MainActivity
import com.proyecto_final.axolingo.R
import com.proyecto_final.axolingo.art.caroussel.CarouselAdapter
import com.proyecto_final.axolingo.art.caroussel.CarouselItem
import com.proyecto_final.axolingo.configuraciones.ConfiguracionesUsuarioActivity
import com.proyecto_final.axolingo.menu_vocabulario.MenuVocabularyActivity
import com.proyecto_final.axolingo.pizarra.InterfazPizarra
import com.proyecto_final.axolingo.selector_palabras.InterfazSelector

// Importa las Activities a las que quieres navegar
import com.proyecto_final.axolingo.leccion_ingles.MenuLeccionInglesActivity
import com.proyecto_final.axolingo.leccion_mate.MenuLeccionMateActivity
import com.proyecto_final.axolingo.art.dialogs.JokeDialog
import com.proyecto_final.axolingo.data.db.AppDatabase
import com.proyecto_final.axolingo.forms.LoginViewModel
import com.proyecto_final.axolingo.menu_principal.MenuPrincipalActivity
import com.proyecto_final.axolingo.session.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch
import kotlin.math.log

// import com.proyecto_final.axolingo.menu_chat.MenuChatActivity // <-- Descomenta cuando la tengas

class MenuPrincipal @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private lateinit var viewPager: ViewPager2
    private lateinit var loginViewModel: LoginViewModel
    private val autoScrollHandler = Handler(Looper.getMainLooper())
    private var autoScrollRunnable: Runnable? = null
    private val AUTO_SCROLL_DELAY_MS = 3000L // 3 segundos

    init {
        inflate(context, R.layout.menu_principal, this)

        // 1. Cargar GIF
        val gifImageView: ImageView = findViewById(R.id.gifImageView)
        Glide.with(this)
            .asGif()
            .load(R.drawable.axo_azul_estudioso_nobg)
            .into(gifImageView)

        // 2. Configurar Carrusel
        setupCarousel()

        // --- NUEVA FUNCIONALIDAD: BOTÓN SETTINGS ---
        // Encontramos el botón por su ID
        val btnSettings: ImageButton = findViewById(R.id.btnSettings)

        // Asignamos el listener para navegar
        btnSettings.setOnClickListener {
            val intent = Intent(context, ConfiguracionesUsuarioActivity::class.java)
            context.startActivity(intent)
        }
        // -------------------------------------------

        // Opcional: Configurar también el botón Home para que refresque o haga algo si es necesario
        val btnHome: ImageButton = findViewById(R.id.btnHome)
        btnHome.setOnClickListener {
            // Como ya estamos en el menú principal, quizás quieras hacer un scroll al inicio
            // o simplemente no hacer nada.
        }

        // --- FUNCIONALIDAD: BOTÓN FAB (Chistes) ---
        val fabButton: ImageButton = findViewById(R.id.btn_chiste)
        fabButton.setOnClickListener {
            JokeDialog(context).show()
        }
        // -------------------------------------------

        val scope = (context as? LifecycleOwner)?.lifecycleScope
        scope?.launch(Dispatchers.IO) {
            val userDao = AppDatabase.getDatabase(context, this).userDao()
            val sessionManager = SessionManager(context)
            loginViewModel = LoginViewModel(userDao, sessionManager)
        }
        val btnLogout: ImageButton = findViewById(R.id.btnLogout)
        btnLogout.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Cerrar sesión")
                .setMessage("¿Seguro que quieres cerrar sesión?")
                .setPositiveButton("Aceptar") { dialog, _ ->
                    dialog.dismiss()
                    loginViewModel.logoutUsuario(
                        onSuccess = {
                            val intent = Intent(context, MainActivity::class.java)
                            context.startActivity(intent)
                            (context as? MenuPrincipalActivity)?.finish()
                        } ,
                        onConflict = {
                            showConflictDialog()
                        }
                    )
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    private fun setupCarousel() {
        viewPager = findViewById(R.id.carouselViewPager)
        val btnLeft: ImageButton = findViewById(R.id.btnCarouselLeft)
        val btnRight: ImageButton = findViewById(R.id.btnCarouselRight)

        // Define los datos que se mostrarán en cada item del carrusel.
        val carouselItems = listOf(
            CarouselItem("Learn Inglés", "Explora nuevas palabras y expande tu conocimiento de esta lengua.", R.drawable.axo_rojo_saltando),
            CarouselItem("Matemáticas con Axo", "Refuerza tu aprendizaje con divertidos minijuegos.", R.drawable.axo_azuk_saltando),
            CarouselItem("Chatea con Axo", "Practica tus habilidades de conversación con nuestro bot.", R.drawable.axo_blanco)
        )

        // --- INICIO DE LA LÓGICA DE NAVEGACIÓN ---
        // Al crear el adaptador, le pasamos un bloque de código (lambda)
        // que recibe el 'clickedItem' sobre el que se hizo clic.
        val adapter = CarouselAdapter(carouselItems) { clickedItem ->

            // Usamos un 'when' (como un switch) para revisar el título del item
            // y decidir qué Activity iniciar.
            val intent = when (clickedItem.title) {
                "Learn Inglés" -> Intent(context, MenuLeccionInglesActivity::class.java)
                "Matemáticas con Axo" -> Intent(context, MenuLeccionMateActivity::class.java)
                "Chatea con Axo" -> null // Reemplaza con: Intent(context, MenuChatActivity::class.java)
                else -> null
            }

            // Inicia la Activity solo si el intent no es nulo.
            intent?.let { context.startActivity(it) }
        }

        viewPager.adapter = adapter
        // --- FIN DE LA LÓGICA DE NAVEGACIÓN ---


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

    private fun showConflictDialog() {
        AlertDialog.Builder(context)
            .setTitle("Cerrar sesión")
            .setMessage("Fallo al cerrar la sesión")
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}

