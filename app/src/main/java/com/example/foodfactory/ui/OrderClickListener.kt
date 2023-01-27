package com.example.foodfactory.ui

import android.view.View
import androidx.recyclerview.widget.RecyclerView

interface OrderClickListener {
    fun onCellClickListener(view: View, holder: RecyclerView.ViewHolder)
}