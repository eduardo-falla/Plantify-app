package com.plantify.plantify_app.model

data class Vendedor(
    val vendedorId: String = "",
    val uid: String = "",
    val nombreTienda: String = "",
    val calificacion: Double = 0.0,
    val totalResenas: Int = 0,
    val descripcion: String = ""
)