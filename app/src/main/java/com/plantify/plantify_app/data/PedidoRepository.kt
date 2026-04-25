package com.plantify.plantify_app.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.plantify.plantify_app.model.Pedido

class PedidoRepository {
    private val db = FirebaseFirestore.getInstance()
    private val pedidosRef = db.collection("pedidos")

    // Crear un nuevo pedido (Compra finalizada)
    fun crearPedido(pedido: Pedido, onComplete: (Boolean) -> Unit) {
        val id = pedidosRef.document().id
        pedidosRef.document(id).set(pedido.copy(pedidoId = id))
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    // Historial de pedidos de un usuario (ordenados por fecha)
    fun obtenerHistorialCompras(compradorId: String, onResult: (List<Pedido>) -> Unit) {
        pedidosRef.whereEqualTo("compradorId", compradorId)
            .orderBy("fecha", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { docs ->
                onResult(docs.toObjects(Pedido::class.java))
            }
            .addOnFailureListener { onResult(emptyList()) }
    }
}