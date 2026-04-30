package com.plantify.plantify_app.model

data class Plant(
    val plantaId: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val cuidados: String = "",
    val precio: Double = 0.0,
    val imagenUrl: String = "",
    val categoria: String = "",
    val vendedorId: String = ""
)