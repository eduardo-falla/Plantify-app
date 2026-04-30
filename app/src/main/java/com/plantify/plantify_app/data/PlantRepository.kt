package com.plantify.plantify_app.data

import com.google.firebase.firestore.FirebaseFirestore
import com.plantify.plantify_app.model.Plant
import kotlinx.coroutines.tasks.await

class PlantRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun getPlants(): Result<List<Plant>> {
        return try {
            val snapshot = db.collection("plantas").get().await()
            val plants = snapshot.documents.mapNotNull {
                it.toObject(Plant::class.java)?.copy(plantaId = it.id)
            }
            Result.success(plants)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}