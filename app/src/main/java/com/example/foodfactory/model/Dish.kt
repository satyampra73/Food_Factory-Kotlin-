package com.example.foodfactory.model

data class Dish(
    var avail: Boolean = true,
    var image: String = "",
    var name: String = "",
    var price: Int = 10,
    var veg: Boolean = true
)