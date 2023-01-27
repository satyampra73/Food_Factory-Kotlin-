package com.example.foodfactory.model

data class OrderView(
    val name: String = "",
    val price: Int = 0,
    val qty: Int = 0,
    val image: String = "",
    val veg: Boolean = false,
    val table: String = "",
    val uid: String = "",
    val orderId: String = "",
    val prepared: Boolean = false
)