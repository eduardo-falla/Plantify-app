package com.plantify.plantify_app.ui.admin

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

class AdminPedidoAdapter(
    private var orders: List<Order>,
    private val onChangeStatus: (Order) -> Unit,
    private val onVerComprobante: (String) -> Unit
) : RecyclerView.Adapter<AdminPedidoAdapter.VH>() {

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvId              : TextView = itemView.findViewById(R.id.tvAdminOrderId)
        val tvEstado          : TextView = itemView.findViewById(R.id.tvAdminOrderEstado)
        val tvTotal           : TextView = itemView.findViewById(R.id.tvAdminOrderTotal)
        val tvFecha           : TextView = itemView.findViewById(R.id.tvAdminOrderFecha)
        val tvItems           : TextView = itemView.findViewById(R.id.tvAdminOrderItems)
        val tvDir             : TextView = itemView.findViewById(R.id.tvAdminOrderDireccion)
        val tvCompradorNombre : TextView = itemView.findViewById(R.id.tvAdminCompradorNombre)
        val tvCompradorEmail  : TextView = itemView.findViewById(R.id.tvAdminCompradorEmail)
        val btnCambiar        : TextView = itemView.findViewById(R.id.btnCambiarEstado)
        val btnComprobante    : TextView = itemView.findViewById(R.id.btnVerComprobante)

        fun bind(order: Order) {
            tvId.text    = "Pedido #${order.pedidoId.take(8).uppercase()}"
            tvTotal.text = "S/ ${"%.2f".format(order.total)}"

            // Comprador
            tvCompradorNombre.text = "👤 ${order.compradorNombre}"
            tvCompradorEmail.text  = "✉️ ${order.compradorEmail}"

            // Productos
            tvItems.text = if (order.items.isEmpty()) {
                "🌿 Sin productos"
            } else {
                "🌿 " + order.items.joinToString(", ") {
                    "${it.nombre} (x${it.quantity})"
                }
            }

            tvDir.text = "📍 ${order.direccion}"

            // Fecha en hora peruana UTC-5
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

            btnCambiar.setOnClickListener { onChangeStatus(order) }

            if (order.comprobanteUrl.isNotEmpty()) {
                btnComprobante.visibility = View.VISIBLE
                btnComprobante.setOnClickListener {
                    onVerComprobante(order.comprobanteUrl)
                }
            } else {
                btnComprobante.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_pedido, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) =
        holder.bind(orders[position])

    override fun getItemCount() = orders.size

    fun updateOrders(newOrders: List<Order>) {
        orders = newOrders
        notifyDataSetChanged()
    }
}