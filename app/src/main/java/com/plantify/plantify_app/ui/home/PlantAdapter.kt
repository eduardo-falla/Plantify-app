package com.plantify.plantify_app.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.plantify.plantify_app.R
import com.plantify.plantify_app.model.Plant

class PlantAdapter(
    private var plants: List<Plant>,
    private val onAddToCart: (Plant) -> Unit
) : RecyclerView.Adapter<PlantAdapter.PlantViewHolder>() {

    class PlantViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivPlant: ImageView = view.findViewById(R.id.ivPlantImage)
        val tvName: TextView    = view.findViewById(R.id.tvPlantName)
        val tvPrice: TextView = view.findViewById(R.id.tvPrice)
        val btnAdd: Button      = view.findViewById(R.id.btnAddToCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_plant_card, parent, false)
        return PlantViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        val plant = plants[position]
        holder.tvName.text  = plant.nombre
        holder.tvPrice.text = "S/ ${"%.2f".format(plant.precio)}"

        Glide.with(holder.itemView.context)
            .load(plant.imagenUrl)
            .placeholder(R.drawable.ic_launcher_foreground)
            .centerCrop()
            .into(holder.ivPlant)

        // Toca la tarjeta → abre detalle
        holder.itemView.setOnClickListener {
            val intent = android.content.Intent(
                holder.itemView.context,
                PlantDetailActivity::class.java
            )
            intent.putExtra("plantaId", plant.plantaId)
            holder.itemView.context.startActivity(intent)
        }

        // Botón "Añadir" → agrega directo al carrito
        holder.btnAdd.setOnClickListener { onAddToCart(plant) }
    }

    override fun getItemCount() = plants.size

    fun updatePlants(newPlants: List<Plant>) {
        plants = newPlants
        notifyDataSetChanged()
    }
}