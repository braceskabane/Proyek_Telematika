package com.dicoding.hanebado.view.welcome

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.hanebado.R

class ViewPagerAdapter(
    private val titles: List<String>,
    private val descriptions: List<String>,
    private val images: List<Int>,
    private val phones: List<Int>
) : RecyclerView.Adapter<ViewPagerAdapter.Pager2ViewHolder>() {

    inner class Pager2ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemTitle: TextView = itemView.findViewById(R.id.tv_description)
        val itemImage: ImageView = itemView.findViewById(R.id.background_image)
        val itemImagePhone: ImageView = itemView.findViewById(R.id.background_image_phone)

        init {
            itemImage.setOnClickListener {
                val position: Int = adapterPosition
                Toast.makeText(
                    itemView.context,
                    "You clicked on item ${position + 1}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Pager2ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_slider_welcome, parent, false)
        return Pager2ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return titles.size
    }

    override fun onBindViewHolder(holder: Pager2ViewHolder, position: Int) {
        holder.itemTitle.text = descriptions[position]
        holder.itemImage.setImageResource(images[position])
        holder.itemImagePhone.setImageResource(phones[position])
    }
}
