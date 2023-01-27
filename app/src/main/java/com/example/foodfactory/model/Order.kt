package com.example.foodfactory.model

data class Order(
    val dish: Dish? = null,
    var qty: Int = 1,
    val uid: String = "",
    val orderId: String = "",
    val time: String = ""
)