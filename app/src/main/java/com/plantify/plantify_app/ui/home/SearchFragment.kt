package com.plantify.plantify_app.ui.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.plantify.plantify_app.R

class SearchFragment : Fragment() {

    private val viewModel: HomeViewModel by activityViewModels()
    private lateinit var adapter: PlantAdapter
    private var todasLasPlantas = listOf<com.plantify.plantify_app.model.Plant>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        val rvMarketplace   = view.findViewById<RecyclerView>(R.id.rvMarketplace)
        val btnTodas        = view.findViewById<Button>(R.id.btnTodas)
        val btnOrnamentales = view.findViewById<Button>(R.id.btnOrnamentales)
        val btnMedicinales  = view.findViewById<Button>(R.id.btnMedicinales)
        val etBuscar        = view.findViewById<EditText>(R.id.etBuscar)

        adapter = PlantAdapter(
            plants = emptyList(),
            onAddToCart = { plant ->
                viewModel.addToCart(plant)
                Toast.makeText(requireContext(), "${plant.nombre} agregado al carrito", Toast.LENGTH_SHORT).show()
            },
            onFavorito = { plant ->
                viewModel.addFavorito(plant)
                Toast.makeText(requireContext(), "${plant.nombre} agregado a favoritos", Toast.LENGTH_SHORT).show()
            },
            onVerDetalle = { plant ->
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, PlantDetailFragment.newInstance(plant))
                    .addToBackStack(null)
                    .commit()
            }
        )

        rvMarketplace.layoutManager = GridLayoutManager(requireContext(), 2)
        rvMarketplace.adapter = adapter

        viewModel.plants.observe(viewLifecycleOwner) { plants ->
            if (plants.isEmpty()) {
                viewModel.loadPlants()
                return@observe
            }
            todasLasPlantas = plants
            val categoria = arguments?.getString("categoria") ?: ""
            if (categoria.isNotEmpty()) filtrar(categoria)
            else adapter.updatePlants(plants)
        }

        btnTodas.setOnClickListener { adapter.updatePlants(todasLasPlantas) }
        btnOrnamentales.setOnClickListener { filtrar("Ornamental") }
        btnMedicinales.setOnClickListener  { filtrar("Medicinal") }

        etBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.isEmpty()) {
                    adapter.updatePlants(todasLasPlantas)
                } else {
                    val filtradas = todasLasPlantas.filter {
                        it.nombre.contains(query, ignoreCase = true)
                    }
                    adapter.updatePlants(filtradas)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        viewModel.loadPlants()

        return view
    }

    private fun filtrar(categoria: String) {
        val filtradas = todasLasPlantas.filter {
            it.categoria.contains(categoria, ignoreCase = true)
        }
        adapter.updatePlants(filtradas)
    }

}