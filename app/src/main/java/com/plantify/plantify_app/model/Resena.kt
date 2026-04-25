package com.plantify.plantify_app.model

data class Resena(
    val resenaId: String = "",
    val vendedorId: String = "",
    val compradorId: String = "",
    val estrellas: Int = 0, // De 1 a 5
    val comentario: String = ""
)