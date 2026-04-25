package com.plantify.plantify_app.model
import com.google.firebase.Timestamp

data class Favorito(
    val favoritoId: String = "",
    val usuarioId: String = "",
    val plantId: String = "",
    val fechaAgregado: Timestamp = Timestamp.now()
)