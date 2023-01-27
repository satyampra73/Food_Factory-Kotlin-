package com.example.foodfactory.ui

import android.content.Context
import android.content.SharedPreferences
import com.example.foodfactory.model.Dish

class Prefs(private val context: Context) {
    private var sp: SharedPreferences = context.getSharedPreferences("Order", Context.MODE_PRIVATE)
    fun addOrder(dish: Dish, qty: Int) {
        sp.edit()
            .putString(dish.name, "${dish.name},${qty},${dish.price},${dish.veg},${dish.image}")
            .apply()
    }

    fun updateOrder(dish: Dish, qty: Int) {
        val data = sp.getString(dish.name, "")
        if (data?.isEmpty() == false) {
            sp.edit()
                .putString(dish.name, "${dish.name},${qty},${dish.price},${dish.veg},${dish.image}")
                .apply()
        }
    }

    fun deleteOrder(name: String) {
        sp.edit().remove(name).apply()
    }

    fun viewOrders(): MutableMap<String, String>? {
        val out = sp.all as MutableMap<String, String>?
        return out
    }

    fun setOrderId(path: String) {
        sp.edit().putString("last_order", path).apply()
    }

    fun getOrderId(): String {
        return sp.getString("last_order", "").toString()
    }

    fun setTable(tableNo: String) {
        sp.edit().putString("table", tableNo).apply()
    }

    fun getTable(): String {
        return sp.getString("table", "").toString()
    }

    fun deleteTable() {
        sp.edit().remove("table").apply()
    }
}