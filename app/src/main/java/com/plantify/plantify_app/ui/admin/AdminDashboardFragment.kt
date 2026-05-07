package com.plantify.plantify_app.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.plantify.plantify_app.R

class AdminDashboardFragment : Fragment() {

    private val viewModel: AdminViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvTotalPlantas      = view.findViewById<TextView>(R.id.tvTotalPlantas)
        val tvTotalPedidos      = view.findViewById<TextView>(R.id.tvTotalPedidos)
        val tvTotalUsuarios     = view.findViewById<TextView>(R.id.tvTotalUsuarios)
        val tvPedidosPendientes = view.findViewById<TextView>(R.id.tvPedidosPendientes)
        val tvEntregados        = view.findViewById<TextView>(R.id.tvPedidosEntregados)
        val tvCancelados        = view.findViewById<TextView>(R.id.tvPedidosCancelados)
        val tvTotalIngresos     = view.findViewById<TextView>(R.id.tvTotalIngresos)
        val tvPlantaMasVendida  = view.findViewById<TextView>(R.id.tvPlantaMasVendida)

        viewModel.totalPlantas.observe(viewLifecycleOwner) {
            tvTotalPlantas.text = it.toString()
        }
        viewModel.totalPedidos.observe(viewLifecycleOwner) {
            tvTotalPedidos.text = it.toString()
        }
        viewModel.totalUsuarios.observe(viewLifecycleOwner) {
            tvTotalUsuarios.text = it.toString()
        }
        viewModel.pedidosPendientes.observe(viewLifecycleOwner) {
            tvPedidosPendientes.text = it.toString()
        }
        viewModel.pedidosEntregados.observe(viewLifecycleOwner) {
            tvEntregados.text = it.toString()
        }
        viewModel.pedidosCancelados.observe(viewLifecycleOwner) {
            tvCancelados.text = it.toString()
        }
        viewModel.totalIngresos.observe(viewLifecycleOwner) {
            tvTotalIngresos.text = "S/ ${"%.2f".format(it)}"
        }
        viewModel.plantaMasVendida.observe(viewLifecycleOwner) {
            tvPlantaMasVendida.text = it
        }

        viewModel.loadDashboard()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadDashboard()
    }
}