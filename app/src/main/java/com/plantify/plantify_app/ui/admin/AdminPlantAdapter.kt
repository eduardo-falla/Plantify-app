package com.plantify.plantify_app.ui.admin

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.plantify.plantify_app.R
import com.plantify.plantify_app.model.Plant

class AdminPlantAdapter(
    private var plants: List<Plant>,
    private val onEdit: (Plant) -> Unit,
    private val onDelete: (Plant) -> Unit
) : RecyclerView.Adapter<AdminPlantAdapter.VH>() {

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivPlant  : ImageView   = itemView.findViewById(R.id.ivAdminPlant)
        val tvNombre : TextView    = itemView.findViewById(R.id.tvAdminPlantName)
        val tvPrecio : TextView    = itemView.findViewById(R.id.tvAdminPlantPrice)
        val tvStock  : TextView    = itemView.findViewById(R.id.tvAdminPlantStock)
        val tvCat    : TextView    = itemView.findViewById(R.id.tvAdminPlantCategory)
        val btnEdit  : ImageButton = itemView.findViewById(R.id.btnEditPlant)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDeletePlant)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        Log.d("AdminAdapter", "onCreateViewHolder llamado")
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_plant, parent, false)
        Log.d("AdminAdapter", "View inflada: ${view.width} x ${view.height}")
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val plant = plants[position]
        Log.d("AdminAdapter", "onBindViewHolder position: $position nombre: ${plant.nombre}")

        holder.tvNombre.text = plant.nombre
        holder.tvPrecio.text = "S/ ${"%.2f".format(plant.precio)}"
        holder.tvStock.text  = "Stock: ${plant.stock}"
        holder.tvCat.text    = plant.categoria

        holder.tvNombre.visibility = View.VISIBLE
        holder.tvPrecio.visibility = View.VISIBLE
        holder.tvStock.visibility  = View.VISIBLE
        holder.tvCat.visibility    = View.VISIBLE

        if (plant.imagenUrl.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(plant.imagenUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .centerCrop()
                .into(holder.ivPlant)
        }

        holder.btnEdit.setOnClickListener   { onEdit(plant) }
        holder.btnDelete.setOnClickListener { onDelete(plant) }
    }

    override fun getItemCount(): Int {
        Log.d("AdminAdapter", "getItemCount: ${plants.size}")
        return plants.size
    }

    fun updatePlants(newPlants: List<Plant>) {
        Log.d("AdminAdapter", "updatePlants: ${newPlants.size} plantas")
        plants = newPlants
        notifyDataSetChanged()
    }
}