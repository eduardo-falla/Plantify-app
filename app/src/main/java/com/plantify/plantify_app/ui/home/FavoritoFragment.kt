package com.plantify.plantify_app.ui.home

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.plantify.plantify_app.R

class FavoritoFragment : Fragment(R.layout.fragment_favorito) {

    private val viewModel: FavoritoViewModel by viewModels()
    private lateinit var adapter: FavoritoAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvFavoritos = view.findViewById<RecyclerView>(R.id.rvFavoritos)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val tvEmpty     = view.findViewById<TextView>(R.id.tvEmpty)

        adapter = FavoritoAdapter(emptyList()) { plant ->
            viewModel.removeFavorito(plant.plantaId)
        }

        rvFavoritos.layoutManager = GridLayoutManager(requireContext(), 2)
        rvFavoritos.adapter = adapter

        viewModel.favoritos.observe(viewLifecycleOwner) { plantas ->
            adapter.updatePlants(plantas)
            tvEmpty.visibility    = if (plantas.isEmpty()) View.VISIBLE else View.GONE
            rvFavoritos.visibility = if (plantas.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        viewModel.loadFavoritos()
    }
}