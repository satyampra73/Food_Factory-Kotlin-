package com.example.foodfactory.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.foodfactory.R
import com.example.foodfactory.model.Dish
import com.example.foodfactory.ui.SubMenuFragment

class subMenuAdapter(
    val dishlist: ArrayList<Dish>,
    private val cellClickListener: SubMenuFragment
) : Adapter<subMenuAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): subMenuAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.dish_item,
            parent, false
        )
        return subMenuAdapter.MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val dish: Dish = dishlist[position]
        holder.subMbinder(dish)
        holder.btn.setOnClickListener {
            cellClickListener.onCellClickListener(it, holder)
            if (holder.btn.isVisible) {
                holder.btn.visibility = View.GONE
                holder.qtycartbtn.visibility = View.VISIBLE
            }
        }
        holder.bmin.setOnClickListener {
            cellClickListener.onCellClickListener(it, holder)
        }
        holder.bplus.setOnClickListener {
            cellClickListener.onCellClickListener(it, holder)
        }

    }

    override fun getItemCount() = dishlist.size

    public class MyViewHolder(itemView: View) : ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.imgprepOrderSubCategory)
        val dish_name: TextView = itemView.findViewById(R.id.textBillSubCategory)
        val price: TextView = itemView.findViewById(R.id.priceBillDish)
        val bmin: TextView = itemView.findViewById(R.id.bmin)
        val qty: TextView = itemView.findViewById(R.id.qty)
        val bplus: TextView = itemView.findViewById(R.id.bplus)
        val veg: ImageView = itemView.findViewById(R.id.vegprepOrder)
        val btn: Button = itemView.findViewById(R.id.cartAddButton)
        val qtycartbtn: LinearLayout = itemView.findViewById(R.id.quantity_Cart)

        init {
            qtycartbtn.visibility = View.VISIBLE
        }

        fun subMbinder(dish: Dish) {
            if (dish.avail == true) {
                btn.tag = dish
                qty.tag = dish
                bplus.tag = dish
                bmin.tag = dish
                dish_name.text = dish.name.toString()
                price.text = "" + dish.price.toString()
                Glide.with(image)
                    .load(dish.image.toString())
                    .placeholder(R.drawable.chef)
                    .into(image)
                if (dish.veg) {
                    Glide.with(veg)
                        .load(R.drawable.veg)
                        .into(veg)
                } else {
                    Glide.with(veg)
                        .load(R.drawable.nonveg)
                        .into(veg)
                }
            }
        }
    }
}

