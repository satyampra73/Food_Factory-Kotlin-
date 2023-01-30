package com.example.foodfactory.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodfactory.R
import com.example.foodfactory.model.Category
import com.example.foodfactory.ui.MenuFragmentDirections

class MenuAdapter(private val fragment: Fragment, val categories: ArrayList<Category>) :
    RecyclerView.Adapter<MenuAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.category_new_layout,
            parent, false
        )
        return MenuAdapter.MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val category: Category = categories[position]
        holder.binder(category, fragment)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    public class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.textCategory)
        val image: ImageView = itemView.findViewById(R.id.imgCategory)
        val wrapper: View = itemView.findViewById(R.id.frame_1)

        fun binder(category: Category, fragment: Fragment) {
            title.text = category.name.toString()
            Glide.with(image)
                .load(category.image.toString())
                .placeholder(R.drawable.chef)
                .into(image)
            wrapper.setOnClickListener {
                val dir =
                    MenuFragmentDirections.actionMenuFragmentToSubMenuFragment(category.name.toString())
                fragment.findNavController().navigate(dir)
            }
        }
    }
}