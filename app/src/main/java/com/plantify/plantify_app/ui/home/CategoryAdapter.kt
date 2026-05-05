package com.plantify.plantify_app.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.plantify.plantify_app.R

class CategoryAdapter(
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.VH>() {

    private var items: List<String> = emptyList()
    private var selectedPosition = 0

    fun submitList(list: List<String>) {
        items = list
        notifyDataSetChanged()
    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvCategoryChip)

        fun bind(name: String, isSelected: Boolean) {
            tvName.text = name
            if (isSelected) {
                tvName.setBackgroundResource(R.drawable.bg_chip_selected)
                tvName.setTextColor(
                    ContextCompat.getColor(itemView.context, R.color.white)
                )
            } else {
                tvName.setBackgroundResource(R.drawable.bg_chip_normal)
                tvName.setTextColor(
                    ContextCompat.getColor(itemView.context, R.color.green_primary)
                )
            }
            itemView.setOnClickListener {
                val prev = selectedPosition
                selectedPosition = adapterPosition
                notifyItemChanged(prev)
                notifyItemChanged(selectedPosition)
                onClick(name)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_category_chip, parent, false)
        )

    override fun onBindViewHolder(holder: VH, position: Int) =
        holder.bind(items[position], position == selectedPosition)

    override fun getItemCount() = items.size
}