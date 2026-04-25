package com.plantify.plantify_app.data

import com.google.firebase.firestore.FirebaseFirestore
import com.plantify.plantify_app.model.Vendedor

class VendedorRepository {
    private val db = FirebaseFirestore.getInstance()
    private val vendedoresRef = db.collection("vendedores")

    // Operación: Registrar vendedor (PK = vendedorId)
    fun registrarVendedor(vendedor: Vendedor, onComplete: (Boolean) -> Unit) {
        // Si el vendedorId está vacío, generamos uno automático
        val id = vendedor.vendedorId.ifEmpty { vendedoresRef.document().id }
        vendedoresRef.document(id).set(vendedor.copy(vendedorId = id))
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    // Operación: Buscar tienda por el ID del usuario (Relación FK)
    fun obtenerVendedorPorUsuario(uid: String, onResult: (Vendedor?) -> Unit) {
        vendedoresRef.whereEqualTo("uid", uid).get()
            .addOnSuccessListener { docs ->
                onResult(docs.toObjects(Vendedor::class.java).firstOrNull())
            }
            .addOnFailureListener { onResult(null) }
    }
}