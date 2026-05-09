package com.plantify.plantify_app.ui.admin

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.plantify.plantify_app.R

class AdminPedidosFragment : Fragment() {

    private val viewModel: AdminViewModel by activityViewModels()
    private lateinit var adapter: AdminPedidoAdapter
    private lateinit var rvPedidos: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_pedidos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvPedidos   = view.findViewById(R.id.rvAdminPedidos)
        progressBar = view.findViewById(R.id.progressBarPedidos)
        tvEmpty     = view.findViewById(R.id.tvEmptyPedidos)

        adapter = AdminPedidoAdapter(
            orders           = emptyList(),
            onChangeStatus   = { order ->
                val estados = arrayOf(
                    "pendiente", "confirmado", "enviado", "entregado", "cancelado"
                )
                val estadosDisplay = arrayOf(
                    "🕐 Pendiente",
                    "✅ Confirmado",
                    "🚚 En camino",
                    "📦 Entregado",
                    "❌ Cancelado"
                )
                androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Cambiar estado del pedido")
                    .setItems(estadosDisplay) { _, which ->
                        viewModel.updateOrderStatus(order.pedidoId, estados[which])
                    }
                    .show()
            },
            onVerComprobante = { url ->
                mostrarComprobante(url)
            }
        )

        rvPedidos.layoutManager = LinearLayoutManager(requireContext())
        rvPedidos.adapter = adapter

        setupObservers()
        viewModel.loadPedidos()
    }

    private fun mostrarComprobante(url: String) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_comprobante)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val ivComprobante = dialog.findViewById<ImageView>(R.id.ivComprobanteDialog)
        val btnCerrar     = dialog.findViewById<TextView>(R.id.btnCerrarDialog)

        Glide.with(requireContext())
            .load(url)
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(ivComprobante)

        btnCerrar.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    private fun setupObservers() {
        viewModel.pedidos.observe(viewLifecycleOwner) { pedidos ->
            adapter.updateOrders(pedidos)
            tvEmpty.visibility   = if (pedidos.isEmpty()) View.VISIBLE else View.GONE
            rvPedidos.visibility = if (pedidos.isEmpty()) View.GONE    else View.VISIBLE
            adapter.notifyDataSetChanged()
            rvPedidos.requestLayout()
            rvPedidos.invalidate()
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
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
        viewModel.loadPedidos()
    }
}