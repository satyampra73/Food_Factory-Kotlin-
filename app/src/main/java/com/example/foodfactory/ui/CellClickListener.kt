package com.example.foodfactory.ui

import android.view.View
import com.example.foodfactory.adapter.subMenuAdapter

interface CellClickListener {
    fun onCellClickListener(view: View, holder: subMenuAdapter.MyViewHolder)
}