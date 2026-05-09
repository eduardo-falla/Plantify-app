package com.plantify.plantify_app.ui.home

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.plantify.plantify_app.R
import com.plantify.plantify_app.model.Plant

class PlantDetailFragment : Fragment(R.layout.fragment_plant_detail) {

    private val viewModel: HomeViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nombre     = arguments?.getString("nombre") ?: ""
        val descripcion = arguments?.getString("descripcion") ?: ""
        val cuidados   = arguments?.getString("cuidados") ?: ""
        val precio     = arguments?.getDouble("precio") ?: 0.0
        val imagenUrl  = arguments?.getString("imagenUrl") ?: ""
        val categoria  = arguments?.getString("categoria") ?: ""

        val plantaId = arguments?.getString("plantaId") ?: ""

        val plant = Plant(
            plantaId = plantaId,
            nombre = nombre,
            descripcion = descripcion,
            cuidados = cuidados,
            precio = precio,
            imagenUrl = imagenUrl,
            categoria = categoria
        )

        view.findViewById<TextView>(R.id.tvDetailNombre).text    = nombre
        view.findViewById<TextView>(R.id.tvDetailPrecio).text    = "S/ ${"%.2f".format(precio)}"
        view.findViewById<TextView>(R.id.tvDetailCategoria).text = categoria
        view.findViewById<TextView>(R.id.tvDetailDescripcion).text = descripcion
        view.findViewById<TextView>(R.id.tvDetailCuidados).text  = cuidados

        Glide.with(this)
            .load(imagenUrl)
            .placeholder(R.drawable.ic_launcher_foreground)
            .centerCrop()
            .into(view.findViewById(R.id.ivDetailImage))

        view.findViewById<ImageButton>(R.id.btnVolver).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        view.findViewById<ImageButton>(R.id.btnFavoritoDetail).setOnClickListener {
            viewModel.addFavorito(plant)
            Toast.makeText(requireContext(), "$nombre agregado a favoritos ❤️", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<Button>(R.id.btnDetailAgregarCarrito).setOnClickListener {
            viewModel.addToCart(plant)
            Toast.makeText(requireContext(), "$nombre agregado al carrito 🌿", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        fun newInstance(plant: Plant) = PlantDetailFragment().apply {
            arguments = Bundle().apply {
                putString("plantaId", plant.plantaId)  // ← agrega esto
                putString("nombre", plant.nombre)
                putString("descripcion", plant.descripcion)
                putString("cuidados", plant.cuidados)
                putDouble("precio", plant.precio)
                putString("imagenUrl", plant.imagenUrl)
                putString("categoria", plant.categoria)
            }
        }
    }
}