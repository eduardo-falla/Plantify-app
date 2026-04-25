package com.plantify.plantify_app.model

data class Planta(
    val plantId: String = "",
    val vendedorId: String = "",
    val nombre: String = "",
    val categoria: String = "",
    val precio: Double = 0.0,
    val descripcion: String = "",
    val imageUrl: String = "",
    val cuidados: String = ""
)