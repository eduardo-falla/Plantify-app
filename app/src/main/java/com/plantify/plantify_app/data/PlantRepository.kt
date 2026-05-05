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
            val plants = snapshot.documents.mapNotNull {
                it.toObject(Plant::class.java)?.copy(plantaId = it.id)
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
            val plants = snapshot.documents.mapNotNull {
                it.toObject(Plant::class.java)?.copy(plantaId = it.id)
            }
            Result.success(plants)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPlantById(plantaId: String): Result<Plant> {
        return try {
            val doc = plantasRef.document(plantaId).get().await()
            val plant = doc.toObject(Plant::class.java)?.copy(plantaId = doc.id)
                ?: return Result.failure(Exception("Planta no encontrada"))
            Result.success(plant)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}