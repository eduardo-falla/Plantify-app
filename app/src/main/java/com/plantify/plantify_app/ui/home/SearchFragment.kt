package com.plantify.plantify_app.ui.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.plantify.plantify_app.R
import com.plantify.plantify_app.data.CartRepository
import com.plantify.plantify_app.model.CartItem
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModels()
    private lateinit var plantAdapter: PlantAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private val cartRepository = CartRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etSearch      = view.findViewById<EditText>(R.id.etSearchReal)
        val rvPlants      = view.findViewById<RecyclerView>(R.id.rvMarketplace)
        val rvCategories  = view.findViewById<RecyclerView>(R.id.rvSearchCategories)
        val progressBar   = view.findViewById<ProgressBar>(R.id.progressBarSearch)
        val tvEmpty       = view.findViewById<TextView>(R.id.tvEmptySearch)
        val tvResultCount = view.findViewById<TextView>(R.id.tvResultCount)

        // ── Adapter categorías ────────────────────────────────
        categoryAdapter = CategoryAdapter { categoria ->
            viewModel.filterByCategory(categoria)
        }
        rvCategories.apply {
            adapter = categoryAdapter
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
        }

        // ── Adapter plantas ───────────────────────────────────
        plantAdapter = PlantAdapter(emptyList()) { plant ->
            viewLifecycleOwner.lifecycleScope.launch {
                val item = CartItem(
                    plantId   = plant.plantaId,
                    nombre    = plant.nombre,
                    precio    = plant.precio,
                    imagenUrl = plant.imagenUrl
                )
                cartRepository.addItem(item)
                    .onSuccess {
                        Toast.makeText(
                            requireContext(),
                            "${plant.nombre} agregado 🌿",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .onFailure {
                        Toast.makeText(
                            requireContext(),
                            "Error al agregar",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
        rvPlants.apply {
            adapter = plantAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }

        // ── Búsqueda en tiempo real ───────────────────────────
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {
                viewModel.search(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // ── Observers ─────────────────────────────────────────
        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        viewModel.results.observe(viewLifecycleOwner) { plants ->
            plantAdapter.updatePlants(plants)
            tvEmpty.visibility       = if (plants.isEmpty()) View.VISIBLE else View.GONE
            rvPlants.visibility      = if (plants.isEmpty()) View.GONE    else View.VISIBLE
            tvResultCount.text       = "${plants.size} resultado(s)"
            tvResultCount.visibility = View.VISIBLE

            // Actualizar chips de categorías
            val cats = viewModel.getCategories()
            if (cats.size > 1) categoryAdapter.submitList(cats)
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }

        viewModel.loadAllPlants()
    }
}