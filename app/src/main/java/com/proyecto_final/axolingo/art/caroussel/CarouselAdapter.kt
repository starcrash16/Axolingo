package com.proyecto_final.axolingo.art.caroussel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.proyecto_final.axolingo.R

import com.proyecto_final.axolingo.art.botons.BotonLeccion

// Adaptador para un carrusel de elementos en un RecyclerView
class CarouselAdapter(
    private val items: List<CarouselItem>, // Lista de elementos a mostrar
    private val onNavigateClick: (CarouselItem) -> Unit // Acción al hacer clic en el botón
) : RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder>() {

    // ViewHolder que representa cada elemento del carrusel
    inner class CarouselViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.carouselItemTitle)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.carouselItemDescription)
        private val imageView: ImageView = itemView.findViewById(R.id.carouselItemImage)
        private val navigateButton: BotonLeccion = itemView.findViewById(R.id.btnNavigate)

        // Vincula los datos del elemento a las vistas
        fun bind(item: CarouselItem, position: Int) {
            titleTextView.text = item.title
            descriptionTextView.text = item.description
            imageView.setImageResource(item.imageResId)

            // Configura la visibilidad y acción del botón
            navigateButton.visibility = View.VISIBLE
            navigateButton.setOnClickListener {
                onNavigateClick(item)
            }
        }
    }

    // Crea una nueva vista para un elemento del carrusel
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_carousel, parent, false)
        return CarouselViewHolder(view)
    }

    // Vincula un elemento de la lista a un ViewHolder
    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    // Devuelve el número total de elementos en la lista
    override fun getItemCount(): Int = items.size
}

