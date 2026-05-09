package com.plantify.plantify_app.ui.home

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.plantify.plantify_app.R


class HistorialFragment : Fragment(R.layout.fragment_historial) {

    private val viewModel: HistorialViewModel by viewModels()
    private lateinit var adapter: HistorialAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvHistorial     = view.findViewById<RecyclerView>(R.id.rvHistorial)
        val progressBar     = view.findViewById<ProgressBar>(R.id.progressBar)
        val tvEmpty         = view.findViewById<TextView>(R.id.tvEmpty)
        val tvPedidoCount   = view.findViewById<TextView>(R.id.tvPedidoCount)

        adapter = HistorialAdapter(emptyList())
        rvHistorial.layoutManager = LinearLayoutManager(requireContext())
        rvHistorial.adapter = adapter

        viewModel.pedidos.observe(viewLifecycleOwner) { pedidos ->
            adapter.updatePedidos(pedidos)
            val isEmpty = pedidos.isEmpty()
            tvEmpty.visibility    = if (isEmpty) View.VISIBLE else View.GONE
            rvHistorial.visibility = if (isEmpty) View.GONE else View.VISIBLE
            val count = pedidos.size
            tvPedidoCount.text = "$count ${if (count == 1) "pedido" else "pedidos"}"
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        viewModel.loadPedidos()
    }
}