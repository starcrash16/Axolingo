package com.proyecto_final.axolingo.art.caroussel


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.proyecto_final.axolingo.R
import com.proyecto_final.axolingo.art.caroussel.CarouselItem

class CarouselAdapter(private val items: List<CarouselItem>) : RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder>() {

    inner class CarouselViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.carouselItemTitle)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.carouselItemDescription)
        private val imageView: ImageView = itemView.findViewById(R.id.carouselItemImage)

        fun bind(item: CarouselItem) {
            titleTextView.text = item.title
            descriptionTextView.text = item.description
            imageView.setImageResource(item.imageResId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_carousel, parent, false)
        return CarouselViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
