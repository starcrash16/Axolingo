package com.proyecto_final.axolingo.art.caroussel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.proyecto_final.axolingo.R

// 1. El constructor ahora acepta una 'lambda' (una función/instrucción) que se llamará al hacer clic.
class CarouselAdapter(
    private val items: List<CarouselItem>,
    private val onNavigateClick: (CarouselItem) -> Unit
) : RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder>() {

    inner class CarouselViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.carouselItemTitle)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.carouselItemDescription)
        private val imageView: ImageView = itemView.findViewById(R.id.carouselItemImage)
        private val navigateButton: Button = itemView.findViewById(R.id.btnNavigate)

        fun bind(item: CarouselItem, position: Int) {
            titleTextView.text = item.title
            descriptionTextView.text = item.description
            imageView.setImageResource(item.imageResId)

            // 2. Lógica para mostrar el botón solo en el primer item.
            if (position == 0) {
                navigateButton.visibility = View.VISIBLE
                // 3. Cuando se hace clic, se ejecuta la instrucción 'onNavigateClick' que recibimos.
                navigateButton.setOnClickListener {
                    onNavigateClick(item)
                }
            } else {
                navigateButton.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_carousel, parent, false)
        return CarouselViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int = items.size
}

