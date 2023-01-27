package com.example.foodfactory.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodfactory.R
import com.example.foodfactory.adapter.BillOrderAdapter.Holder
import com.example.foodfactory.model.OrderView

class BillOrderAdapter(
    val billList: ArrayList<OrderView>
) : RecyclerView.Adapter<Holder>() {
    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dish_name: TextView = itemView.findViewById(R.id.textBillSubCategory)
        val price: TextView = itemView.findViewById(R.id.priceBillDish)
        val qty: TextView = itemView.findViewById(R.id.priceBillQty)

        fun bind(bill: OrderView) {
            dish_name.text = bill.name
            qty.text = bill.qty.toString()
            price.text = bill.price.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.orderlist_item, parent, false)
        return Holder(itemView)

    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val bill = billList[position]
        holder.bind(bill)
    }

    override fun getItemCount() = billList.size


}