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

        val tvVerTodo = view.findViewById<TextView>(R.id.tvVerTodo)
        tvVerTodo.setOnClickListener {
            (activity as? HomeActivity)?.navigateToSearch()
        }

        val searchBar = view.findViewById<View>(R.id.searchBarClickable)
        searchBar.setOnClickListener {
            (activity as? HomeActivity)?.navigateToSearch()
        }

        val rvRecommended = view.findViewById<RecyclerView>(R.id.rvRecommended)
        adapter = PlantAdapter(emptyList()) { plant ->
            viewModel.addToCart(plant)
            Toast.makeText(requireContext(), "${plant.nombre} agregado al carrito 🌿", Toast.LENGTH_SHORT).show()
        }
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