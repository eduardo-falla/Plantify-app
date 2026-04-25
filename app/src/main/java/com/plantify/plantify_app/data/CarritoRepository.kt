package com.plantify.plantify_app.data

import com.google.firebase.firestore.FirebaseFirestore
import com.plantify.plantify_app.model.Carrito

class CarritoRepository {
    private val db = FirebaseFirestore.getInstance()
    private val carritoRef = db.collection("carrito")

    // Agregar o actualizar item en el carrito
    fun agregarAlCarrito(item: Carrito, onComplete: (Boolean) -> Unit) {
        val id = item.carritoId.ifEmpty { carritoRef.document().id }
        carritoRef.document(id).set(item.copy(carritoId = id))
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    // Obtener el carrito de un usuario específico
    fun obtenerCarrito(usuarioId: String, onResult: (List<Carrito>) -> Unit) {
        carritoRef.whereEqualTo("usuarioId", usuarioId).get()
            .addOnSuccessListener { docs ->
                onResult(docs.toObjects(Carrito::class.java))
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    // Limpiar carrito tras una compra
    fun eliminarItem(carritoId: String, onComplete: (Boolean) -> Unit) {
        carritoRef.document(carritoId).delete()
            .addOnSuccessListener { onComplete(true) }
    }
}