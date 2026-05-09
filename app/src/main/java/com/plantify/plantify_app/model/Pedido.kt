package com.plantify.plantify_app.model

data class Pedido(
    val pedidoId: String = "",
    val usuarioId: String = "",
    val items: List<CartItem> = emptyList(),
    val total: Double = 0.0,
    val estado: String = "pendiente",
    val fecha: Long = System.currentTimeMillis()
)