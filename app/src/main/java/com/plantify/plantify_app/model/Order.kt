package com.plantify.plantify_app.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Order(
    val pedidoId: String = "",
    val compradorId: String = "",
    val compradorNombre: String = "",
    val compradorEmail: String = "",
    val items: List<CartItem> = emptyList(),
    val total: Double = 0.0,
    val estado: String = "pendiente",
    val direccion: String = "",
    val metodoPago: String = "QR",
    val comprobanteUrl: String = "",
    @ServerTimestamp val fecha: Date? = null
)

object OrderEstado {
    const val PENDIENTE  = "pendiente"
    const val CONFIRMADO = "confirmado"
    const val ENVIADO    = "enviado"
    const val ENTREGADO  = "entregado"
    const val CANCELADO  = "cancelado"
}