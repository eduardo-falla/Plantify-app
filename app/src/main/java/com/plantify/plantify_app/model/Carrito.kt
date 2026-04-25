package com.plantify.plantify_app.model

data class Carrito(
    val carritoId: String = "",
    val usuarioId: String = "",
    val plantId: String = "",
    val cantidad: Int = 0,
    val precioUnitario: Double = 0.0
)