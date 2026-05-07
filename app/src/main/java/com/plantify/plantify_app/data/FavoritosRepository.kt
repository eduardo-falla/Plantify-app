package com.plantify.plantify_app.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.plantify.plantify_app.model.Plant
import kotlinx.coroutines.tasks.await

class FavoritosRepository {

    private val db   = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun favoritosRef() = db.collection("usuarios")
        .document(auth.currentUser!!.uid)
        .collection("favoritos")

    // Obtener todos los favoritos
    suspend fun getFavoritos(): Result<List<Plant>> {
        return try {
            val snapshot = favoritosRef().get().await()
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
                } catch (e: Exception) { null }
            }
            Result.success(plants)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Agregar a favoritos
    suspend fun agregarFavorito(plant: Plant): Result<Unit> {
        return try {
            val data = hashMapOf<String, Any>(
                "nombre"      to plant.nombre,
                "descripcion" to plant.descripcion,
                "cuidados"    to plant.cuidados,
                "precio"      to plant.precio,
                "imagenUrl"   to plant.imagenUrl,
                "categoria"   to plant.categoria,
                "stock"       to plant.stock
            )
            favoritosRef().document(plant.plantaId).set(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Quitar de favoritos
    suspend fun quitarFavorito(plantaId: String): Result<Unit> {
        return try {
            favoritosRef().document(plantaId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Verificar si es favorito
    suspend fun esFavorito(plantaId: String): Boolean {
        return try {
            val doc = favoritosRef().document(plantaId).get().await()
            doc.exists()
        } catch (e: Exception) {
            false
        }
    }
}