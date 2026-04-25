package com.plantify.plantify_app.model
import com.google.firebase.Timestamp

data class Pedido(
    val pedidoId: String = "",
    val compradorId: String = "",
    val items: List<ItemPedido> = emptyList(),
    val total: Double = 0.0,
    val estado: String = "pendiente",
    val fecha: Timestamp = Timestamp.now()
)

data class ItemPedido(
    val plantId: String = "",
    val nombre: String = "",
    val cantidad: Int = 0,
    val precioUnitario: Double = 0.0
)