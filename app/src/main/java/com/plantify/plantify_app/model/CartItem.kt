package com.plantify.plantify_app.model
data class CartItem(
    val id: String = "",
    val plantId: String = "",
    val nombre: String = "",
    val precio: Double = 0.0,
    val imagenUrl: String = "",
    var quantity: Int = 1
)