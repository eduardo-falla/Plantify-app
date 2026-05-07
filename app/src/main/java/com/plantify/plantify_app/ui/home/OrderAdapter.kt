package com.plantify.plantify_app.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.plantify.plantify_app.R
import com.plantify.plantify_app.model.Order
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class OrderAdapter(
    private var orders: List<Order>
) : RecyclerView.Adapter<OrderAdapter.VH>() {

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvOrderId   : TextView = itemView.findViewById(R.id.tvOrderId)
        val tvFecha     : TextView = itemView.findViewById(R.id.tvOrderFecha)
        val tvTotal     : TextView = itemView.findViewById(R.id.tvOrderTotal)
        val tvEstado    : TextView = itemView.findViewById(R.id.tvOrderEstado)
        val tvItems     : TextView = itemView.findViewById(R.id.tvOrderItems)
        val tvDireccion : TextView = itemView.findViewById(R.id.tvOrderDireccion)

        fun bind(order: Order) {
            tvOrderId.text = "Pedido #${order.pedidoId.take(8).uppercase()}"
            tvTotal.text   = "S/ ${"%.2f".format(order.total)}"

            // Productos con cantidad
            tvItems.text = if (order.items.isEmpty()) {
                "🌿 Sin productos"
            } else {
                "🌿 " + order.items.joinToString(", ") {
                    "${it.nombre} (x${it.quantity})"
                }
            }

            // Dirección
            tvDireccion.text = "📍 ${order.direccion}"

            // Fecha en hora peruana
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("America/Lima")
            tvFecha.text = order.fecha?.let { "📅 ${sdf.format(it)}" } ?: "📅 Sin fecha"

            // Estado
            tvEstado.text = when (order.estado) {
                "pendiente"  -> "🕐 Pendiente"
                "confirmado" -> "✅ Confirmado"
                "enviado"    -> "🚚 En camino"
                "entregado"  -> "📦 Entregado"
                "cancelado"  -> "❌ Cancelado"
                else         -> order.estado
            }

            val color = when (order.estado) {
                "pendiente"  -> 0xFFFF9800.toInt()
                "confirmado" -> 0xFF4CAF50.toInt()
                "enviado"    -> 0xFF2196F3.toInt()
                "entregado"  -> 0xFF1B4332.toInt()
                "cancelado"  -> 0xFFEF4444.toInt()
                else         -> 0xFF9E9E9E.toInt()
            }
            tvEstado.setTextColor(color)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        Log.d("OrderAdapter", "onCreateViewHolder llamado")
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        Log.d("OrderAdapter", "onBindViewHolder position: $position")
        holder.bind(orders[position])
    }

    override fun getItemCount() = orders.size

    fun updateOrders(newOrders: List<Order>) {
        orders = newOrders
        notifyDataSetChanged()
    }
}