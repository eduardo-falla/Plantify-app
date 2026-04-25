package com.plantify.plantify_app.data

import com.google.firebase.firestore.FirebaseFirestore
import com.plantify.plantify_app.model.Resena

class ResenaRepository {
    private val db = FirebaseFirestore.getInstance()
    private val resenasRef = db.collection("resenas")

    fun publicarResena(resena: Resena, onComplete: (Boolean) -> Unit) {
        val id = resenasRef.document().id
        resenasRef.document(id).set(resena.copy(resenaId = id))
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun obtenerResenasDeTienda(vendedorId: String, onResult: (List<Resena>) -> Unit) {
        resenasRef.whereEqualTo("vendedorId", vendedorId).get()
            .addOnSuccessListener { docs ->
                onResult(docs.toObjects(Resena::class.java))
            }
            .addOnFailureListener { onResult(emptyList()) }
    }
}