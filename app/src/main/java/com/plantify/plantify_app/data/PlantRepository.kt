package com.plantify.plantify_app.data

import com.google.firebase.firestore.FirebaseFirestore
import com.plantify.plantify_app.model.Plant
import kotlinx.coroutines.tasks.await

class PlantRepository {

    private val db = FirebaseFirestore.getInstance()
    private val plantasRef = db.collection("plantas")

    suspend fun getPlants(): Result<List<Plant>> {
        return try {
            val snapshot = plantasRef.get().await()
            val plants = snapshot.documents.mapNotNull { doc ->
                try {
                    Plant(
                        plantaId    = doc.id,
                        nombre      = doc.getString("nombre") ?: "",
                        descripcion = doc.getString("descripcion") ?: "",
                        cuidados    = doc.getString("cuidados") ?: "",
                        precio      = doc.getDouble("precio") ?: 0.0,
                        imagenUrl   = doc.getString("imagenUrl") ?: "",
                        categoria   = doc.getString("categoria") ?: "",
                        stock       = doc.getLong("stock")?.toInt() ?: 0
                    )
                } catch (e: Exception) {
                    null
                }
            }
            Result.success(plants)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPlantsByCategory(categoria: String): Result<List<Plant>> {
        return try {
            val snapshot = plantasRef
                .whereEqualTo("categoria", categoria)
                .get().await()
            val plants = snapshot.documents.mapNotNull { doc ->
                try {
                    Plant(
                        plantaId    = doc.id,
                        nombre      = doc.getString("nombre") ?: "",
                        descripcion = doc.getString("descripcion") ?: "",
                        cuidados    = doc.getString("cuidados") ?: "",
                        precio      = doc.getDouble("precio") ?: 0.0,
                        imagenUrl   = doc.getString("imagenUrl") ?: "",
                        categoria   = doc.getString("categoria") ?: "",
                        stock       = doc.getLong("stock")?.toInt() ?: 0
                    )
                } catch (e: Exception) {
                    null
                }
            }
            Result.success(plants)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPlantById(plantaId: String): Result<Plant> {
        return try {
            val doc = plantasRef.document(plantaId).get().await()
            if (!doc.exists()) {
                return Result.failure(Exception("Planta no encontrada"))
            }
            val plant = Plant(
                plantaId    = doc.id,
                nombre      = doc.getString("nombre") ?: "",
                descripcion = doc.getString("descripcion") ?: "",
                cuidados    = doc.getString("cuidados") ?: "",
                precio      = doc.getDouble("precio") ?: 0.0,
                imagenUrl   = doc.getString("imagenUrl") ?: "",
                categoria   = doc.getString("categoria") ?: "",
                stock       = doc.getLong("stock")?.toInt() ?: 0
            )
            Result.success(plant)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}