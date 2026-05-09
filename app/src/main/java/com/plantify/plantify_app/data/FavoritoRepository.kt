package com.plantify.plantify_app.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.plantify.plantify_app.model.Plant
import kotlinx.coroutines.tasks.await

class FavoritoRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun favoritosCollection() =
        db.collection("usuarios")
            .document(auth.currentUser?.uid ?: "guest")
            .collection("favoritos")

    suspend fun getFavoritos(): Result<List<Plant>> {
        return try {
            val snapshot = favoritosCollection().get().await()
            val plantas = snapshot.documents.mapNotNull {
                it.toObject(Plant::class.java)?.copy(plantaId = it.id)
            }
            Result.success(plantas)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addFavorito(plant: Plant): Result<Unit> {
        return try {
            favoritosCollection().document(plant.plantaId).set(plant).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeFavorito(plantaId: String): Result<Unit> {
        return try {
            favoritosCollection().document(plantaId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun isFavorito(plantaId: String): Boolean {
        return try {
            favoritosCollection().document(plantaId).get().await().exists()
        } catch (e: Exception) {
            false
        }
    }
}