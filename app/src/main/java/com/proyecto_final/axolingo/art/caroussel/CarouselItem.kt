package com.proyecto_final.axolingo.art.caroussel

import androidx.annotation.DrawableRes

data class CarouselItem(
    val title: String,
    val description: String,
    @DrawableRes val imageResId: Int
)
