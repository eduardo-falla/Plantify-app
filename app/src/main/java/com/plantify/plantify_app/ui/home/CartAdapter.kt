package com.plantify.plantify_app.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.plantify.plantify_app.R
import com.plantify.plantify_app.model.CartItem

class CartAdapter(
    private var items: List<CartItem>,
    private val onIncrease: (CartItem) -> Unit,
    private val onDecrease: (CartItem) -> Unit,
    private val onRemove: (CartItem) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivPlant: ImageView       = view.findViewById(R.id.ivPlant)
        val tvName: TextView         = view.findViewById(R.id.tvName)
        val tvPrice: TextView        = view.findViewById(R.id.tvPrice)
        val tvQuantity: TextView     = view.findViewById(R.id.tvQuantity)
        val tvTotalItem: TextView    = view.findViewById(R.id.tvTotalItem)
        val btnIncrease: ImageButton = view.findViewById(R.id.btnIncrease)
        val btnDecrease: ImageButton = view.findViewById(R.id.btnDecrease)
        val btnRemove: ImageButton   = view.findViewById(R.id.btnRemove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = items[position]

        holder.tvName.text      = item.nombre
        holder.tvPrice.text     = "S/ ${"%.2f".format(item.precio)} c/u"
        holder.tvQuantity.text  = item.cantidad.toString()
        holder.tvTotalItem.text = "S/ ${"%.2f".format(item.precio * item.cantidad)}"

        Glide.with(holder.itemView.context)
            .load(item.imagenUrl)
            .placeholder(R.drawable.ic_launcher_foreground)
            .centerCrop()
            .into(holder.ivPlant)

        holder.btnIncrease.setOnClickListener { onIncrease(item) }
        holder.btnDecrease.setOnClickListener { onDecrease(item) }
        holder.btnRemove.setOnClickListener   { onRemove(item) }
    }

    override fun getItemCount() = items.size

    fun updateItems(newItems: List<CartItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}