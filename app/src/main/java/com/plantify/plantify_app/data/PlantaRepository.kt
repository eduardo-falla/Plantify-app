package com.plantify.plantify_app.data

import com.google.firebase.firestore.FirebaseFirestore
import com.plantify.plantify_app.model.Planta

class PlantaRepository {
    private val db = FirebaseFirestore.getInstance()
    private val plantasRef = db.collection("plantas")

    // Operación: Subir nueva planta (PK = plantId)
    fun subirPlanta(planta: Planta, onComplete: (Boolean) -> Unit) {
        val id = planta.plantId.ifEmpty { plantasRef.document().id }
        plantasRef.document(id).set(planta.copy(plantId = id))
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    // Operación: Listar todas las plantas de un vendedor específico (Usando FK)
    fun obtenerPlantasPorVendedor(vendedorId: String, onResult: (List<Planta>) -> Unit) {
        plantasRef.whereEqualTo("vendedorId", vendedorId).get()
            .addOnSuccessListener { docs ->
                onResult(docs.toObjects(Planta::class.java))
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    // Operación: Catálogo general (Todas las plantas)
    fun obtenerCatalogo(onResult: (List<Planta>) -> Unit) {
        plantasRef.get()
            .addOnSuccessListener { docs ->
                onResult(docs.toObjects(Planta::class.java))
            }
            .addOnFailureListener { onResult(emptyList()) }
    }
}