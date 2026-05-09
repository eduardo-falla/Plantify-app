package com.plantify.plantify_app.ui.admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.plantify.plantify_app.R

class AdminPlantasFragment : Fragment() {

    private val viewModel: AdminViewModel by activityViewModels()
    private lateinit var adapter: AdminPlantAdapter
    private lateinit var rvPlantas: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_plantas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Bind views
        rvPlantas   = view.findViewById(R.id.rvAdminPlantas)
        progressBar = view.findViewById(R.id.progressBarPlantas)
        tvEmpty     = view.findViewById(R.id.tvEmptyPlantas)
        val fabAgregar = view.findViewById<FloatingActionButton>(R.id.fabAgregarPlanta)

        // 2. Crear adapter
        adapter = AdminPlantAdapter(
            plants   = emptyList(),
            onEdit   = { plant ->
                val intent = Intent(requireContext(), AddEditPlantActivity::class.java)
                intent.putExtra("plantaId", plant.plantaId)
                intent.putExtra("esEdicion", true)
                startActivity(intent)
            },
            onDelete = { plant ->
                androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Eliminar planta")
                    .setMessage("¿Eliminar ${plant.nombre}?")
                    .setPositiveButton("Eliminar") { _, _ ->
                        viewModel.deletePlant(plant.plantaId)
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        )

        // 3. Asignar LayoutManager Y adapter ANTES de observar
        rvPlantas.layoutManager = LinearLayoutManager(requireContext())
        rvPlantas.adapter = adapter

        Log.d("AdminPlantas", "LayoutManager asignado: ${rvPlantas.layoutManager}")
        Log.d("AdminPlantas", "Adapter asignado: ${rvPlantas.adapter}")

        // 4. FAB
        fabAgregar.setOnClickListener {
            startActivity(Intent(requireContext(), AddEditPlantActivity::class.java))
        }

        // 5. Observers
        setupObservers()

        // 6. Cargar datos
        Log.d("AdminPlantas", "Llamando loadPlantas()")
        viewModel.loadPlantas()
    }

    private fun setupObservers() {
        viewModel.plantas.observe(viewLifecycleOwner) { plants ->
            Log.d("AdminPlantas", "Plantas recibidas: ${plants.size}")
            Log.d("AdminPlantas", "Primera planta: ${plants.firstOrNull()?.nombre}")

            adapter.updatePlants(plants)

            Log.d("AdminPlantas", "Adapter itemCount: ${adapter.itemCount}")
            Log.d("AdminPlantas", "rvPlantas height: ${rvPlantas.height}")
            Log.d("AdminPlantas", "rvPlantas visibility: ${rvPlantas.visibility}")

            tvEmpty.visibility   = if (plants.isEmpty()) View.VISIBLE else View.GONE
            rvPlantas.visibility = if (plants.isEmpty()) View.GONE    else View.VISIBLE

            adapter.notifyDataSetChanged()
            rvPlantas.requestLayout()
            rvPlantas.invalidate()
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            Log.d("AdminPlantas", "Loading: $loading")
            progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        viewModel.message.observe(viewLifecycleOwner) { msg ->
            msg?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.clearMessage()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("AdminPlantas", "onResume - recargando plantas")
        viewModel.loadPlantas()
    }
}