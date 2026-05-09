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
import com.plantify.plantify_app.model.Plant

class FavoritoAdapter(
    private var plants: List<Plant>,
    private val onRemove: (Plant) -> Unit
) : RecyclerView.Adapter<FavoritoAdapter.FavoritoViewHolder>() {

    class FavoritoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivPlant: ImageView    = view.findViewById(R.id.ivPlantImage)
        val tvName: TextView      = view.findViewById(R.id.tvPlantName)
        val tvPrice: TextView     = view.findViewById(R.id.tvPrice)
        val btnFavorito: ImageButton = view.findViewById(R.id.btnFavorito)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_plant_fav, parent, false)
        return FavoritoViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoritoViewHolder, position: Int) {
        val plant = plants[position]
        holder.tvName.text  = plant.nombre
        holder.tvPrice.text = "S/ ${"%.2f".format(plant.precio)}"

        Glide.with(holder.itemView.context)
            .load(plant.imagenUrl)
            .placeholder(R.drawable.ic_launcher_foreground)
            .centerCrop()
            .into(holder.ivPlant)

        holder.btnFavorito.setOnClickListener { onRemove(plant) }
    }

    override fun getItemCount() = plants.size

    fun updatePlants(newPlants: List<Plant>) {
        plants = newPlants
        notifyDataSetChanged()
    }
}