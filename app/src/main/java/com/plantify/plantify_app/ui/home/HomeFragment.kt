package com.plantify.plantify_app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.plantify.plantify_app.R

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var adapter: PlantAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Saludo con nombre del usuario
        val tvGreeting = view.findViewById<TextView>(R.id.tvGreeting)
        viewModel.getNombreUsuario { nombre ->
            tvGreeting.text = "¡Hola, $nombre!"
        }

        val tvVerTodo = view.findViewById<TextView>(R.id.tvVerTodo)
        tvVerTodo.setOnClickListener {
            (activity as? HomeActivity)?.navigateToSearch()
        }

        val searchBar = view.findViewById<View>(R.id.searchBarClickable)
        searchBar.setOnClickListener {
            (activity as? HomeActivity)?.navigateToSearch()
        }

        view.findViewById<View>(R.id.btnCategoriaOrnamentales).setOnClickListener {
            (activity as? HomeActivity)?.navigateToSearch("Ornamental")
        }

        view.findViewById<View>(R.id.btnCategoriaMedicinales).setOnClickListener {
            (activity as? HomeActivity)?.navigateToSearch("Medicinal")
        }

        val rvRecommended = view.findViewById<RecyclerView>(R.id.rvRecommended)
        adapter = PlantAdapter(
            plants = emptyList(),
            onAddToCart = { plant ->
                viewModel.addToCart(plant)
                Toast.makeText(requireContext(), "${plant.nombre} agregado al carrito 🌿", Toast.LENGTH_SHORT).show()
            },
            onFavorito = { plant ->
                viewModel.addFavorito(plant)
                Toast.makeText(requireContext(), "${plant.nombre} agregado a favoritos ❤️", Toast.LENGTH_SHORT).show()
            },
            onVerDetalle = { plant ->
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, PlantDetailFragment.newInstance(plant))
                    .addToBackStack(null)
                    .commit()
            }
        )
        rvRecommended.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.HORIZONTAL, false
        )
        rvRecommended.adapter = adapter

        viewModel.plants.observe(viewLifecycleOwner) { plants ->
            adapter.updatePlants(plants)
        }

        viewModel.loadPlants()

        return view
    }
}