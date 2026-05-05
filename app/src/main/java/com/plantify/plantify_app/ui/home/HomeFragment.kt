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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.plantify.plantify_app.R

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var plantAdapter: PlantAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCategoryRecycler(view)
        setupPlantRecycler(view)
        setupObservers(view)

        // "Ver todo" → navega a Buscar
        view.findViewById<TextView>(R.id.tvVerTodo).setOnClickListener {
            (activity as? HomeActivity)?.navigateToSearch()
        }

        // SearchBar → navega a Buscar
        view.findViewById<View>(R.id.searchBarClickable).setOnClickListener {
            (activity as? HomeActivity)?.navigateToSearch()
        }

        viewModel.loadPlants()
    }

    private fun setupCategoryRecycler(view: View) {
        categoryAdapter = CategoryAdapter { categoria ->
            viewModel.filterByCategory(categoria)
        }
        view.findViewById<RecyclerView>(R.id.rvCategories).apply {
            adapter = categoryAdapter
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
        }
    }

    private fun setupPlantRecycler(view: View) {
        plantAdapter = PlantAdapter(emptyList()) { plant ->
            viewModel.addToCart(plant)
        }
        view.findViewById<RecyclerView>(R.id.rvRecommended).apply {
            adapter = plantAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    private fun setupObservers(view: View) {
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val tvEmpty     = view.findViewById<TextView>(R.id.tvEmpty)
        val rvPlants    = view.findViewById<RecyclerView>(R.id.rvRecommended)

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        viewModel.categories.observe(viewLifecycleOwner) { cats ->
            categoryAdapter.submitList(cats)
        }

        viewModel.filteredPlants.observe(viewLifecycleOwner) { plants ->
            plantAdapter.updatePlants(plants)
            tvEmpty.visibility  = if (plants.isEmpty()) View.VISIBLE else View.GONE
            rvPlants.visibility = if (plants.isEmpty()) View.GONE   else View.VISIBLE
        }

        viewModel.cartMessage.observe(viewLifecycleOwner) { msg ->
            msg?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.clearCartMessage()
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
    }
}