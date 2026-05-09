package com.plantify.plantify_app.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.plantify.plantify_app.R
import com.plantify.plantify_app.model.Pedido
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistorialAdapter(
    private var pedidos: List<Pedido>
) : RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder>() {

    class HistorialViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvPedidoId: TextView    = view.findViewById(R.id.tvPedidoId)
        val tvEstado: TextView      = view.findViewById(R.id.tvEstado)
        val tvFecha: TextView       = view.findViewById(R.id.tvFecha)
        val tvProductos: TextView   = view.findViewById(R.id.tvProductos)
        val tvTotalPedido: TextView = view.findViewById(R.id.tvTotalPedido)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorialViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_historial, parent, false)
        return HistorialViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistorialViewHolder, position: Int) {
        val pedido = pedidos[position]

        holder.tvPedidoId.text    = "Pedido #${pedido.pedidoId.takeLast(6).uppercase()}"
        holder.tvEstado.text      = pedido.estado.replaceFirstChar { it.uppercase() }
        holder.tvTotalPedido.text = "S/ ${"%.2f".format(pedido.total)}"

        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        holder.tvFecha.text = sdf.format(Date(pedido.fecha))

        val productos = pedido.items.joinToString("\n") {
            "• ${it.nombre} x${it.cantidad} — S/ ${"%.2f".format(it.precio * it.cantidad)}"
        }
        holder.tvProductos.text = productos
    }

    override fun getItemCount() = pedidos.size

    fun updatePedidos(newPedidos: List<Pedido>) {
        pedidos = newPedidos
        notifyDataSetChanged()
    }
}