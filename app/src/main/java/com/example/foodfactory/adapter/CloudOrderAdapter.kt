package com.example.foodfactory.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodfactory.R
import com.example.foodfactory.model.OrderView
import com.example.foodfactory.ui.OrderFragment

class CloudOrderAdapter(
    val orderList: ArrayList<OrderView>,
    private val orderClickListener: OrderFragment
) : RecyclerView.Adapter<CloudOrderAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.prep_order, parent, false
        )
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val order = orderList[position]
        holder.bind(order)
    }

    override fun getItemCount() = orderList.size

    public class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.imgprepOrderSubCategory)
        val dish_name: TextView = itemView.findViewById(R.id.textBillSubCategory)
        val price: TextView = itemView.findViewById(R.id.priceBillDish)
        val qty: TextView = itemView.findViewById(R.id.txtprepOrderQty)

        val veg: ImageView = itemView.findViewById(R.id.vegprepOrder)

        fun bind(order: OrderView) {
            dish_name.text = order.name
            qty.text = order.qty.toString()
            price.text = order.price.toString()
            Glide.with(image)
                .load(order.image)
                .placeholder(R.drawable.chef)
                .into(image)
            if (order.veg){
                Glide.with(veg)
                    .load(R.drawable.veg)
                    .placeholder(R.drawable.veg)
                    .into(veg)
            }
            else {
                Glide.with(veg)
                    .load(R.drawable.nonveg)
                    .placeholder(R.drawable.nonveg)
                    .into(veg)
            }
        }
    }
}